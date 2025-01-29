
import { crypto } from "k6/experimental/webcrypto";
import encoding from 'k6/encoding';
import { check } from 'k6';
import http from 'k6/http';

const vus = 500;
const baseUrl = "http://uid2-prod-opr-use2-alb-698161474.us-east-2.elb.amazonaws.com";
const clientSecret = "";
const clientKey = "";

const generateVUs = vus;
const refreshVUs = vus;
const identityMapVUs = vus;
const keySharingVUs = vus;
const testDuration = '10m'

//30 warm up on each
// 5 min each
// 12, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600
// 13 scenarios, each 5.5 min = 4290 se

export const options = {
  insecureSkipTLSVerify: true,
  noConnectionReuse: false,
  scenarios: {
    // Warmup scenarios
    tokenGenerateWarmup: {
      executor: 'ramping-vus',
      exec: 'tokenGenerate',
      stages: [
        { duration: '30s', target: generateVUs}
      ],
      gracefulRampDown: '0s',
    },
    tokenRefreshWarmup: {
      executor: 'ramping-vus',
      exec: 'tokenRefresh',
      stages: [
        { duration: '30s', target: refreshVUs}
      ],
      gracefulRampDown: '0s',
    },
    identityMapWarmup: {
      executor: 'ramping-vus',
      exec: 'identityMap',
      stages: [
        { duration: '30s', target: identityMapVUs}
      ],
      gracefulRampDown: '0s',
    },
    keySharingWarmup: {
      executor: 'ramping-vus',
      exec: 'keySharing',
      stages: [
        { duration: '30s', target: keySharingVUs}
      ],
      gracefulRampDown: '0s',
    },
    // Actual testing scenarios
    tokenGenerate: {
      executor: 'constant-vus',
      exec: 'tokenGenerate',
      vus: generateVUs,
      duration: testDuration,
      gracefulStop: '0s',
      startTime: '30s',
    },
    tokenRefresh: {
      executor: 'constant-vus',
      exec: 'tokenRefresh',
      vus: refreshVUs,
      duration: testDuration,
      gracefulStop: '0s',
      startTime: '30s',
    },
    identityMap: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: identityMapVUs,
      duration: testDuration,
      gracefulStop: '0s',
      startTime: '30s',
    },
    keySharing:{
      executor: 'constant-vus',
      exec: 'keySharing',
      vus: keySharingVUs,
      duration: testDuration,
      gracefulStop: '0s',
      startTime: '30s',
    }/*,
    identityMapLargeBatchSequential: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 1,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '970s',
    },
    identityMapLargeBatch: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 16,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '1280s',
    },
    identityBuckets: {
      executor: 'constant-vus',
      exec: 'identityBuckets',
      vus: 2,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '1590s',
    },*/
  },
  // So we get count in the summary, to demonstrate different metrics are different
  summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)', 'count'],
  thresholds: {
    // Intentionally empty. We'll programatically define our bogus
    // thresholds (to generate the sub-metrics) below. In your real-world
    // load test, you can add any real threshoulds you want here.
  }
};

// https://community.k6.io/t/multiple-scenarios-metrics-per-each/1314/3
for (let key in options.scenarios) {
  // Each scenario automaticall tags the metrics it generates with its own name
  let thresholdName = `http_req_duration{scenario:${key}}`;
  // Check to prevent us from overwriting a threshold that already exists
  if (!options.thresholds[thresholdName]) {
    options.thresholds[thresholdName] = [];
  }
  // 'max>=0' is a bogus condition that will always be fulfilled
  options.thresholds[thresholdName].push('max>=0');
}

export async function setup() {
  var token = await generateRefreshRequest();
  return {
    tokenGenerate: null,
    identityMap: null,
    refreshToken: token
  };

  async function generateRefreshRequest() {
    let request = await createReq( {'optout_check': 1, 'email': 'test5000@example.com'});
    var requestData = {
      endpoint: '/v2/token/generate',
      requestBody: request,
    }
    let response = await send(requestData, clientKey);
    let decrypt = await decryptEnvelope(response.body, clientSecret)
    return decrypt.body.refresh_token;
  };
}

export function handleSummary(data) {
  return {
    'summary.json': JSON.stringify(data),
  }
}

// Scenarios
export async function tokenGenerate(data) {
  const endpoint = '/v2/token/generate';
  if (data.tokenGenerate == null) {
    var newData = await generateTokenGenerateRequestWithTime();
    data.tokenGenerate = newData;
  } else if (data.tokenGenerate.time < (Date.now() - 45000)) {
    data.tokenGenerate = await generateTokenGenerateRequestWithTime();
  }

  var requestBody = data.tokenGenerate.requestBody;
  var tokenGenerateData = {
    endpoint: endpoint,
    requestBody: requestBody,
  }

  execute(tokenGenerateData, true);
}

export function tokenRefresh(data) {
  var requestBody = data.refreshToken;
  var refreshData = {
    endpoint: '/v2/token/refresh',
    requestBody: requestBody
  }

  execute(refreshData, false);
}

export async function identityMap(data) {
  const endpoint = '/v2/identity/map';
  if ((data.identityMap == null) || (data.identityMap.time < (Date.now() - 45000))) {
    data.identityMap = await generateIdentityMapRequestWithTime(2);;
  }

  var requestBody = data.identityMap.requestBody;
  var identityData = {
    endpoint: endpoint,
    requestBody: requestBody,
  }
  execute(identityData, true);
}

export async function identityMapLargeBatch(data) {
  const endpoint = '/v2/identity/map';
  if ((data.identityMap == null) || (data.identityMap.time < (Date.now() - 45000))) {
    data.identityMap = await generateIdentityMapRequestWithTime(5000);;
  }

  var requestBody = data.identityMap.requestBody;
  var identityData = {
    endpoint: endpoint,
    requestBody: requestBody,
  }
  execute(identityData, true);
}

export function identityBuckets(data) {
  var requestData = data.identityBuckets.requestData;
  var elementToUse = selectRequestData(requestData);

  var bucketData = {
    endpoint: data.identityBuckets.endpoint,
    requestBody: elementToUse.requestBody,
  }
  execute(bucketData, true);
}

export async function keySharing(data) {
  const endpoint = '/v2/key/sharing';
  if (data.keySharing == null) {
    var newData = await generateKeySharingRequestWithTime();
    data.keySharing = newData;
  } else if (data.tokenGenerate.time < (Date.now() - 45000)) {
    data.keySharing = await generateKeySharingRequestWithTime();
  }

  var requestBody = data.keySharing.requestBody;
  var keySharingData = {
    endpoint: endpoint,
    requestBody: requestBody,
  }

  execute(keySharingData, true);
}

// Helpers
async function createReqWithTimestamp(timestampArr, obj) {
  var envelope = getEnvelopeWithTimestamp(timestampArr, obj);
  return encoding.b64encode((await encryptEnvelope(envelope, clientSecret)).buffer);
}

function generateIdentityMapRequest(emailCount) {
  var data = {
    'optout_check': 1,
    "email": []
  };

  for (var i = 0; i < emailCount; ++i) {
    data.email.push(`test${i}@example.com`);
  }

  return data;
}

function send(data, auth) {
  var options = {};
  if (auth) {
    options.headers = {
      'Authorization': `Bearer ${clientKey}`
    };
  }

  return http.post(`${baseUrl}${data.endpoint}`, data.requestBody, options);
}

function execute(data, auth) {
  var response = send(data, auth);

  check(response, {
    'status is 200': r => r.status === 200,
  });
}

async function encryptEnvelope(envelope, clientSecret) {
  const rawKey = encoding.b64decode(clientSecret);
  const key = await crypto.subtle.importKey("raw", rawKey, "AES-GCM", true, [
    "encrypt",
    "decrypt",
  ]);

  const iv = crypto.getRandomValues(new Uint8Array(12));

  const ciphertext = new Uint8Array(await crypto.subtle.encrypt(
    {
      name: "AES-GCM",
      iv: iv,
    },
    key,
    envelope
  ));

  const result = new Uint8Array(+(1 + iv.length + ciphertext.length));

  // The version of the envelope format.
  result[0] = 1;

  result.set(iv, 1);

  // The tag is at the end of ciphertext.
  result.set(ciphertext, 1 + iv.length);

  return result;
}

async function decryptEnvelope(envelope, clientSecret) {
  const rawKey = encoding.b64decode(clientSecret);
  const rawData = encoding.b64decode(envelope);
  const key = await crypto.subtle.importKey("raw", rawKey, "AES-GCM", true, [
    "encrypt",
    "decrypt",
  ]);
  const length = rawData.byteLength;
  const iv = rawData.slice(0, 12);

  const decrypted = await crypto.subtle.decrypt(
    {
      name: "AES-GCM",
      iv: iv,
      tagLength: 128
    },
    key,
    rawData.slice(12)
  );


  const decryptedResponse = String.fromCharCode.apply(String, new Uint8Array(decrypted.slice(16)));
  const response = JSON.parse(decryptedResponse);

  return response;
}

function getEnvelopeWithTimestamp(timestampArray, obj) {
  var randomBytes = new Uint8Array(8);
  crypto.getRandomValues(randomBytes);

  var payload = stringToUint8Array(JSON.stringify(obj));

  var envelope = new Uint8Array(timestampArray.length + randomBytes.length + payload.length);
  envelope.set(timestampArray);
  envelope.set(randomBytes, timestampArray.length);
  envelope.set(payload, timestampArray.length + randomBytes.length);

  return envelope;

}
function getEnvelope(obj) {
  var timestampArr = new Uint8Array(getTimestamp());
  return getEnvelopeWithTimestamp(timestampArr, obj);
}

function getTimestamp() {
  const now = Date.now();
  return getTimestampFromTime(now);
}

function getTimestampFromTime(time) {
  const res = new ArrayBuffer(8);
  const { hi, lo } = Get32BitPartsBE(time);
  const view = new DataView(res);
  view.setUint32(0, hi, false);
  view.setUint32(4, lo, false);
  return res;
}

// http://anuchandy.blogspot.com/2015/03/javascript-how-to-extract-lower-32-bit.html
function Get32BitPartsBE(bigNumber) {
  if (bigNumber > 9007199254740991) {
    // Max int that JavaScript can represent is 2^53.
    throw new Error('The 64-bit value is too big to be represented in JS :' + bigNumber);
  }

  var bigNumberAsBinaryStr = bigNumber.toString(2);
  // Convert the above binary str to 64 bit (actually 52 bit will work) by padding zeros in the left
  var bigNumberAsBinaryStr2 = '';
  for (var i = 0; i < 64 - bigNumberAsBinaryStr.length; i++) {
    bigNumberAsBinaryStr2 += '0';
  };

  bigNumberAsBinaryStr2 += bigNumberAsBinaryStr;

  return {
    hi: parseInt(bigNumberAsBinaryStr2.substring(0, 32), 2),
    lo: parseInt(bigNumberAsBinaryStr2.substring(32), 2),
  };
}

function stringToUint8Array(str) {
  const buffer = new ArrayBuffer(str.length);
  const view = new Uint8Array(buffer);
  for (var i = 0; i < str.length; i++) {
    view[i] = str.charCodeAt(i);
  }
  return view;
}

async function createReq(obj) {
  var envelope = getEnvelope(obj);
  return encoding.b64encode((await encryptEnvelope(envelope, clientSecret)).buffer);
};

async function generateRequestWithTime(obj) {
  var time = Date.now();
  var timestampArr = new Uint8Array(getTimestampFromTime(time));
  var requestBody = await createReqWithTimestamp(timestampArr, obj);
  var element = {
    time: time,
    requestBody: requestBody
  };

  return element;
}


async function generateTokenGenerateRequestWithTime() {
  let requestData = { 'optout_check': 1, 'email': 'test500@example.com' };
  return await generateRequestWithTime(requestData);
}

async function generateIdentityMapRequestWithTime(emailCount) {
  let emails = generateIdentityMapRequest(emailCount);
  return await generateRequestWithTime(emails);
}

async function generateKeySharingRequestWithTime() {
  let requestData = { };
  return await generateRequestWithTime(requestData);
}

const generateSinceTimestampStr = () => {
  var date = new Date(Date.now() - 2 * 24 * 60 * 60 * 1000 /* 2 days ago */);
  var year = date.getFullYear();
  var month = (date.getMonth() + 1).toString().padStart(2, '0');
  var day = date.getDate().toString().padStart(2, '0');

  return `${year}-${month}-${day}T00:00:00`;
};
