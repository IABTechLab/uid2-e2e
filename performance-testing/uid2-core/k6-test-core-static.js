import http from 'k6/http';

const CORE_API_TOKEN = __ENV.OPERATOR_KEY; // Mock token for E2E tests
const CORE_BASE_URL = __ENV.CORE_URL;

const AUTH_HEADERS = { headers: { 'Authorization': `Bearer ${CORE_API_TOKEN}` }};
const HEADERS = { headers: { ...AUTH_HEADERS.headers, 'X-UID2-AppVersion': 'uid2-operator=0.0.0-local-load-test' }};

const DURATION = '1m';
const RATE_MULTIPLIER = 10;

export const options = {
    scenarios: {
        ops_healthcheck: {
            executor: 'constant-arrival-rate',
            exec: 'opsHealthcheck',
            duration: DURATION,
            rate: 4 * RATE_MULTIPLIER,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
        },

        attest: {
            executor: 'constant-arrival-rate',
            exec: 'attest',
            duration: DURATION,
            rate: 1 * RATE_MULTIPLIER,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
        },

        clients_refresh: {
            executor: 'constant-arrival-rate',
            exec: 'clientsRefresh',
            duration: DURATION,
            rate: 16 * RATE_MULTIPLIER,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
        },
        keysets_refresh: {
            executor: 'constant-arrival-rate',
            exec: 'keysetRefresh',
            duration: DURATION,
            rate: 16 * RATE_MULTIPLIER,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
        },
        keyset_keys_refresh: {
            executor: 'constant-arrival-rate',
            exec: 'keysetKeysRefresh',
            duration: DURATION,
            rate: 16 * RATE_MULTIPLIER,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
        },
        salt_refresh: {
            executor: 'constant-arrival-rate',
            exec: 'saltRefresh',
            duration: DURATION,
            rate: 16 * RATE_MULTIPLIER,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
        },

        sites_refresh: {
            executor: 'constant-arrival-rate',
            exec: 'sitesRefresh',
            duration: DURATION,
            rate: 9 * RATE_MULTIPLIER,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
        },
        cstg_refresh: {
            executor: 'constant-arrival-rate',
            exec: 'clientSideKeypairsRefresh',
            duration: DURATION,
            rate: 9 * RATE_MULTIPLIER,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
        },
        services_refresh: {
            executor: 'constant-arrival-rate',
            exec: 'servicesRefresh',
            duration: DURATION,
            rate: 9 * RATE_MULTIPLIER,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
        },
        service_links_refresh: {
            executor: 'constant-arrival-rate',
            exec: 'serviceLinksRefresh',
            duration: DURATION,
            rate: 9 * RATE_MULTIPLIER,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
        },
    },
};

// Scenarios
export async function opsHealthcheck(data) {
    http.get(`${CORE_BASE_URL}/ops/healthcheck`);
}

export async function attest(data) {
    const validTrustedAttestationRequest = '{"attestation_request":"AA==","public_key":"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl7kXK1wf15V9HgkQhbMK2nfJGbudmdXNrX7MZjFm07z6eEjaQsuqMteQumLRwn+RxEcXKVaxBAE3RQTFL9XsZc2OtRKOU+oMIQep8tmPFMgh83BzjLs5O5HZf510geFJO6qRqc3UHJT3ACxE7IkmRx1JIKFKPzTrthHdb2+D7bdJBPsVbwk7y+a36f5jvELGGzMC89LAvd7JpOmGsCAj6jwiEAKmmLId9bfe0YeLuebl95VfSzrQVdz82oGGQKXuJgKZtc/Xp1omZ9spm+zzVFJzsimxDdGdnaWMnas43VoTE04JDt+pucJTTbftIvu05frwkbZh3sQ2yBu5gBP7YwIDAQAB","application_name":"uid2-operator","application_version":"5.27.10-3f25586306","components":{"uid2-attestation-api":"2.0.0-f968aec0e3","uid2-shared":"7.2.4-SNAPSHOT"}}';
    http.post(`${CORE_BASE_URL}/attest`, validTrustedAttestationRequest, AUTH_HEADERS);
}

export async function clientsRefresh(data) {
    http.get(`${CORE_BASE_URL}/clients/refresh`, HEADERS);
}

export async function keysetRefresh(data) {
    http.get(`${CORE_BASE_URL}/key/keyset/refresh`, HEADERS);
}

export async function keysetKeysRefresh(data) {
    http.get(`${CORE_BASE_URL}/key/keyset-keys/refresh`, HEADERS);
}

export async function saltRefresh(data) {
    http.get(`${CORE_BASE_URL}/salt/refresh`, HEADERS);
}

export async function sitesRefresh(data) {
    http.get(`${CORE_BASE_URL}/sites/refresh`, HEADERS);
}

export async function clientSideKeypairsRefresh(data) {
    http.get(`${CORE_BASE_URL}/client_side_keypairs/refresh`, HEADERS);
}

export async function servicesRefresh(data) {
    http.get(`${CORE_BASE_URL}/services/refresh`, HEADERS);
}

export async function serviceLinksRefresh(data) {
    http.get(`${CORE_BASE_URL}/service_links/refresh`, HEADERS);
}
