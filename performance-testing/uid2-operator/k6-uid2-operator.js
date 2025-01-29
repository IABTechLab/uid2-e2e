import {crypto} from "k6/experimental/webcrypto";
import encoding from 'k6/encoding';
import {check} from 'k6';
import http from 'k6/http';

const testDurationInSeconds = 2500;
const tokenGenerateTests = true;
const tokenRefreshTests = true;
const identityMapTests = true;
const identityBucketTests = true;
const keySharingTests = true;


//30 warm up on each
// 5 min each
// 12, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600
// 13 scenarios, each 5.5 min = 4290 se

export const options = {
  insecureSkipTLSVerify: true,  
  noConnectionReuse: true,
  scenarios: {
    // Warmup scenarios
    tokenGenerateWarmup: {
      executor: 'constant-vus',
      exec: 'tokenGenerate',
      vus: 300,
      duration: '30s',
      gracefulStop: '0s',
    },
    tokenRefreshWarmup: {
      executor: 'constant-vus',
      exec: 'tokenRefresh',
      vus: 300,
      duration: '30s',
      gracefulStop: '0s',
    },
    identityMapWarmup: {
      executor: 'constant-vus',
      exec: 'identityMap',
      vus: 300,
      duration: '30s',
      gracefulStop: '0s',
    },
    identityBucketsWarmup: {
      executor: 'constant-vus',
      exec: 'identityBuckets',
      vus: 2,
      duration: '30s',
      gracefulStop: '0s',
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
      vus: 300,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '40s',
    },
    tokenRefresh: {
      executor: 'constant-vus',
      exec: 'tokenRefresh',
      vus: 300,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '350s',
    },
    identityMap: {
      executor: 'constant-vus',
      exec: 'identityMap',
      vus: 300,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '660s',
    },
    keySharing:{
      executor: 'constant-vus',
      exec: 'keySharing',
      vus: 300,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '30s',
    },
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
    },
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

// Configs
const clientSecret = __ENV.CLIENT_SECRET;
const clientKey = __ENV.CLIENT_KEY;
const baseUrl = __ENV.BASE_URL;

export async function setup() {
  // pregenerate the envelopes so they don't expire, but can be reused. Means the load test is not constrained by the client
  // Each is used fopr 45 sec. Add 2 to ensure we have enough
  const numberOfRequestsToGenerate = Math.round(testDurationInSeconds / 45) + 2;


  async function generateRefreshRequest(data) {
    let request = await createReq( {'optout_check': 1, 'email': 'test5000@example.com'});
    var requestData = {
      endpoint: '/v2/token/generate',
      requestBody: request,
    }
    let response = await send(requestData, clientKey);
    let decrypt = await decryptEnvelope(response.body, clientSecret)
    return decrypt.body.refresh_token;
  };



  let tokenGenerateData = {};
  if (tokenGenerateTests) {
    tokenGenerateData = {
      endpoint: '/v2/token/generate',
      requestData: await generateFutureGenerateRequests(numberOfRequestsToGenerate),
    };
  }
  let tokenRefreshData = {};
  if(tokenRefreshTests) {
    tokenRefreshData = {
      endpoint: '/v2/token/refresh',
      requestBody: await generateRefreshRequest(tokenGenerateData),
    };
  }

  let identityMapData = {};
  let identityMapLargeBatchData = {};
  if(identityMapTests) {
    identityMapData = {
      endpoint: '/v2/identity/map',
      requestData: await generateFutureMapRequest(numberOfRequestsToGenerate, 2)
    };
    identityMapLargeBatchData = {
      requestData: await generateFutureMapRequest(numberOfRequestsToGenerate, 5000)
    };
  }

  let identityBucketData = {}
  if(identityBucketTests) {
    identityBucketData = {
      endpoint: '/v2/identity/buckets',
      requestData: await generateFutureBucketRequests(numberOfRequestsToGenerate)
    };
  }

  let keySharingData = {};
  if(keySharingTests) {
    keySharingData = {
      endpoint: '/v2/key/sharing',
      requestData: await generateFutureKeySharingRequests(numberOfRequestsToGenerate)
    };
  }

  return {
    tokenGenerate: tokenGenerateData,
    tokenRefresh: tokenRefreshData,
    identityMap: identityMapData,
    identityMapLargeBatch: identityMapLargeBatchData,
    identityBuckets: identityBucketData,
    keySharing: keySharingData
  };
}

// Scenarios
export function tokenGenerate(data) {
  var requestData = data.tokenGenerate.requestData;
  var elementToUse = selectRequestData(requestData);

  var tokenGenerateData = {
    endpoint: data.tokenGenerate.endpoint,
    requestBody: elementToUse.requestBody,
  }
  execute(tokenGenerateData, true);
}

export function tokenRefresh(data) {
  execute(data.tokenRefresh, false);
}

export function identityMap(data) {
  var requestData = data.identityMap.requestData;
  var elementToUse = selectRequestData(requestData);

  var identityData = {
    endpoint: '/v2/identity/map',
    requestBody: elementToUse.requestBody,
  }
  execute(identityData, true);
}

export function identityMapLargeBatch(data) {
  var requestData = data.identityMapLargeBatch.requestData;
  var elementToUse = selectRequestData(requestData);

  var identityData = {
    endpoint: '/v2/identity/map',
    requestBody: elementToUse.requestBody,
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
  } else if (data.keySharing.time < (Date.now() - 45000)) {
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
function selectRequestData(requestData) {
  var elementToUse = requestData[0];
  for (var i = 0; i < requestData.length; i++) {
    var currentTime = Date.now() + 5000;
    if (currentTime > requestData[i].time && currentTime < requestData[i + 1].time) {
      elementToUse = requestData[i];
      //console.log("VU: " + exec.vu.idInTest + ", item: " + i);
      break;
    }
  }
  return elementToUse;
}

async function createReq(obj) {
  var envelope = getEnvelope(obj);
  return encoding.b64encode((await encryptEnvelope(envelope, clientSecret)).buffer);
};

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

async function generateFutureRequests(count, obj) {
  const result = [];
  for (var i = 0; i < count; i++) {
    var time = Date.now() + (i * 45000)
    var timestampArr = new Uint8Array(getTimestampFromTime(time));
    var requestBody = await createReqWithTimestamp(timestampArr, obj);
    var element = {
      time: time,
      requestBody: requestBody
    };
    result.push(element);
  }
  return result;
}

async function generateFutureMapRequest(count, emailCount) {
  let emails = generateIdentityMapRequest(emailCount);
  return await generateFutureRequests(count, emails);
}

async function generateFutureGenerateRequests(count) {
  let obj = {'optout_check': 1, 'email': 'test500@example.com'};
  return await generateFutureRequests(count, obj)
}

async function generateFutureKeySharingRequests(count) {
  let obj = { };
  return await generateFutureRequests(count, obj);
}

const generateSinceTimestampStr = () => {
  var date = new Date(Date.now() - 2 * 24 * 60 * 60 * 1000 /* 2 days ago */);
  var year = date.getFullYear();
  var month = (date.getMonth() + 1).toString().padStart(2, '0');
  var day = date.getDate().toString().padStart(2, '0');

  return `${year}-${month}-${day}T00:00:00`;
};

async function generateFutureBucketRequests(count) {
  let obj = {"since_timestamp": generateSinceTimestampStr()};
  return await generateFutureRequests(count, obj)
}
