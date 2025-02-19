import http from 'k6/http';

const CORE_API_TOKEN = "UID2-O-L-999-dp9Dt0.JVoGpynN4J8nMA7FxmzsavxJa8B9H74y9xdEE="; // Mock token for E2E tests
const CORE_BASE_URL = 'http://localhost:8088';

const AUTH_HEADERS = { headers: { 'Authorization': `Bearer ${CORE_API_TOKEN}` }};
const HEADERS = { headers: { ...AUTH_HEADERS.headers, 'X-UID2-AppVersion': 'uid2-operator=0.0.0-local-load-test' }};

const STAGE_DURATION = '120s';
const STAGE_LOAD_MULTIPLIER = [
    1,
    2,
    5,
    10,
];

export const options = {
    scenarios: {
        ops_healthcheck: {
            executor: 'ramping-arrival-rate',
            exec: 'opsHealthcheck',
            startRate: 4,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
            stages: getStages(4),
        },

        attest: {
            executor: 'ramping-arrival-rate',
            exec: 'attest',
            startRate: 1,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
            stages: getStages(1),
        },

        clients_refresh: {
            executor: 'ramping-arrival-rate',
            exec: 'clientsRefresh',
            startRate: 1,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
            stages: getStages(16),
        },
        keysets_refresh: {
            executor: 'ramping-arrival-rate',
            exec: 'keysetRefresh',
            startRate: 1,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
            stages: getStages(16),
        },
        keyset_keys_refresh: {
            executor: 'ramping-arrival-rate',
            exec: 'keysetKeysRefresh',
            startRate: 1,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
            stages: getStages(16),
        },
        salt_refresh: {
            executor: 'ramping-arrival-rate',
            exec: 'saltRefresh',
            startRate: 1,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
            stages: getStages(16),
        },

        sites_refresh: {
            executor: 'ramping-arrival-rate',
            exec: 'sitesRefresh',
            startRate: 1,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
            stages: getStages(9),
        },
        cstg_refresh: {
            executor: 'ramping-arrival-rate',
            exec: 'clientSideKeypairsRefresh',
            startRate: 1,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
            stages: getStages(9),
        },
        services_refresh: {
            executor: 'ramping-arrival-rate',
            exec: 'servicesRefresh',
            startRate: 1,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
            stages: getStages(9),
        },
        service_links_refresh: {
            executor: 'ramping-arrival-rate',
            exec: 'serviceLinksRefresh',
            startRate: 1,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
            stages: getStages(9),
        },
    },
};

function getStages(qps) {
    return STAGE_LOAD_MULTIPLIER.map(loadMultiplier => ({
        duration: STAGE_DURATION,
        target: qps * loadMultiplier
    }));
}

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
