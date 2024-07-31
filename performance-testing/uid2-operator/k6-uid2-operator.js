import { crypto } from "k6/experimental/webcrypto";
import encoding from 'k6/encoding';
import { check } from 'k6';
import http from 'k6/http';
import exec from 'k6/execution';

const testDurationInSeconds = 4300;
const targetVUCount = 100;

//30 warm up on each
// 5 min each
// 12, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600
// 13 scenarios, each 5.5 min = 4290 se

export const options = {
  noConnectionReuse: false,
  scenarios: {
    // Warmup scenarios
    identityMapWarmup12: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 12 }
      ],
      exec: 'identityMapLargeBatch',
      gracefulStop: '0s',
    },
    identityMapLargeBatch12: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 12,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '30s',
    },
    identityMapWarmup50: {
      executor: 'ramping-vus',
      startVUs: 12,
      stages: [
        { duration: '30s', target: 50 }
      ],
      exec: 'identityMapLargeBatch',
      gracefulStop: '0s',
      startTime: '330s'
    },
    identityMapLargeBatch50: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 50,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '360s',
    },
    identityMapWarmup100: {
      executor: 'ramping-vus',
      startVUs: 50,
      stages: [
        { duration: '30s', target: 100 }
      ],
      exec: 'identityMapLargeBatch',
      gracefulStop: '0s',
      startTime: '660s',
    },
    identityMapLargeBatch100: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 100,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '690s',
    },
    identityMapWarmup150: {
      executor: 'ramping-vus',
      startVUs: 100,
      stages: [
        { duration: '30s', target: 150 }
      ],
      exec: 'identityMapLargeBatch',
      gracefulStop: '0s',
      startTime: '990s',
    },
    identityMapLargeBatch150: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 150,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '1020s',
    },
    identityMapWarmup200: {
      executor: 'ramping-vus',
      startVUs: 150,
      stages: [
        { duration: '30s', target: 200 }
      ],
      exec: 'identityMapLargeBatch',
      gracefulStop: '0s',
      startTime: '1320s',
    },
    identityMapLargeBatch200: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 200,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '1350s',
    },
    identityMapWarmup250: {
      executor: 'ramping-vus',
      startVUs: 200,
      stages: [
        { duration: '30s', target: 250 }
      ],
      exec: 'identityMapLargeBatch',
      gracefulStop: '0s',
      startTime: '1650s',
    },
    identityMapLargeBatch250: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 250,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '1680s',
    },
    identityMapWarmup300: {
      executor: 'ramping-vus',
      startVUs: 250,
      stages: [
        { duration: '30s', target: 300 }
      ],
      exec: 'identityMapLargeBatch',
      gracefulStop: '0s',
      startTime: '1980s',
    },
    identityMapLargeBatch300: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 300,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '2010s',
    },
    identityMapWarmup350: {
      executor: 'ramping-vus',
      startVUs: 300,
      stages: [
        { duration: '30s', target: 350 }
      ],
      exec: 'identityMapLargeBatch',
      gracefulStop: '0s',
      startTime: '2310s',
    },
    identityMapLargeBatch350: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 350,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '2340s',
    },
    identityMapWarmup400: {
      executor: 'ramping-vus',
      startVUs: 350,
      stages: [
        { duration: '30s', target: 400 }
      ],
      exec: 'identityMapLargeBatch',
      gracefulStop: '0s',
      startTime: '2640s',
    },
    identityMapLargeBatch400: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 400,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '2670s',
    },
    identityMapWarmup450: {
      executor: 'ramping-vus',
      startVUs: 400,
      stages: [
        { duration: '30s', target: 450 }
      ],
      exec: 'identityMapLargeBatch',
      gracefulStop: '0s',
      startTime: '2970s',
    },
    identityMapLargeBatch450: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 450,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '3000s',
    },
    identityMapWarmup500: {
      executor: 'ramping-vus',
      startVUs: 450,
      stages: [
        { duration: '30s', target: 500 }
      ],
      exec: 'identityMapLargeBatch',
      gracefulStop: '0s',
      startTime: '3300s',
    },
    identityMapLargeBatch500: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 500,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '3330s',
    },
    identityMapWarmup550: {
      executor: 'ramping-vus',
      startVUs: 500,
      stages: [
        { duration: '30s', target: 550 }
      ],
      exec: 'identityMapLargeBatch',
      gracefulStop: '0s',
      startTime: '3630s',
    },
    identityMapLargeBatch550: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 550,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '3660s',
    },
    identityMapWarmup600: {
      executor: 'ramping-vus',
      startVUs: 550,
      stages: [
        { duration: '30s', target: 600 }
      ],
      exec: 'identityMapLargeBatch',
      gracefulStop: '0s',
      startTime: '3960s',
    },
    identityMapLargeBatch600: {
      executor: 'constant-vus',
      exec: 'identityMapLargeBatch',
      vus: 600,
      duration: '300s',
      gracefulStop: '0s',
      startTime: '3990s',
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


  const generateRefreshRequest = (data) => {
    // TODO: call tokenGenerate to get the refresh token from response
    const refreshToken = __ENV.REFRESH_TOKEN;
    return refreshToken;
  };

  const generateSinceTimestampStr = () => {
    var date = new Date(Date.now() - 2 * 24 * 60 * 60 * 1000 /* 2 days ago */);
    var year = date.getFullYear();
    var month = (date.getMonth() + 1).toString().padStart(2, '0');
    var day = date.getDate().toString().padStart(2, '0');

    return `${year}-${month}-${day}T00:00:00`;
  };

  const tokenGenerateData = {
    endpoint: '/v2/token/generate',
    requestBody: await createReq({
      'optout_check': 1,
      'email': 'test@example.com',
    }),
  };

  return {
    tokenGenerate: tokenGenerateData,
    tokenRefresh: {
      endpoint: '/v2/token/refresh',
      requestBody: generateRefreshRequest(tokenGenerateData),
    },
    identityMap: {
      endpoint: '/v2/identity/map',
      requestBody: await createReq(generateIdentityMapRequest(2)),
    },
    identityMapLargeBatch: {
      requestData: await generateFutureMapRequest(numberOfRequestsToGenerate)
    },
    identityBuckets: {
      endpoint: '/v2/identity/buckets',
      requestBody: await createReq({
        "since_timestamp": generateSinceTimestampStr(),
      }),
    }
  };
}

// Scenarios
export function tokenGenerate(data) {
  execute(data.tokenGenerate, true);
}

export function tokenRefresh(data) {
  execute(data.tokenRefresh, false);
}

export function identityMap(data) {
  execute(data.identityMap, true);
}

export function identityMapLargeBatch(data) {
  var requestData = data.identityMapLargeBatch.requestData;
  var elementToUse = requestData[0];
  for (var i = 0; i < requestData.length; i++) {
    var currentTime = Date.now() + 5000;
    if (currentTime > requestData[i].time && currentTime < requestData[i + 1].time) {
      elementToUse = requestData[i];
      //console.log("VU: " + exec.vu.idInTest + ", item: " + i);
      break;
    }
  }

  var identityData = {
    endpoint: '/v2/identity/map',
    requestBody: elementToUse.requestBody,
  }

   execute(identityData, true);
}

export function identityBuckets(data) {
  execute(data.identityBuckets, true);
}

// Helpers
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

async function generateFutureMapRequest(count) {
  const result = [];
  for (var i = 0; i < count; i++) {
    var time = Date.now() + (i * 45000)
    var timestampArr = new Uint8Array(getTimestampFromTime(time));
    var emails = generateIdentityMapRequest(5000);
    var requestBody = await createReqWithTimestamp(timestampArr, emails);
    var element = {
      time: time,
      requestBody: requestBody
    };
    result.push(element);
  }
  return result;
}