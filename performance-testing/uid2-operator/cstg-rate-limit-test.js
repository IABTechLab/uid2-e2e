import http from "k6/http";
import { check } from 'k6'

const hosts = {
    prod: {
        uid2: {
            global: "https://prod.uidapi.com",
            au: "https://au.prod.uidapi.com",
            jp: "https://jp.prod.uidapi.com",
            sg: "https://sg.prod.uidapi.com",
            usw: "https://usw.prod.uidapi.com",
            globalAccelerator: "https://global.prod.uidapi.com"
        }
    }
}

//change the const below to test different hosts
const host = hosts.prod.uid2.usw;

export const options = {
    scenarios: {
        createTraffic: {
            executor: "shared-iterations",
            vus: 200,
            iterations: 15000,
            startTime: "0s",
            exec: "createTraffic"
        },
        assertIsBlocked: {
            executor: "shared-iterations",
            vus: 10,
            iterations: 1000,
            startTime: "120s",
            exec: "assertIsBlocked"
        },
    },
};

const makeRequest = () => {
    const params = {
        headers: {
            "User-Agent": "k6",
            "Origin": 'k6-test-origin'
        }
    };

    return http.post(host + "/v2/token/client-generate", null, params);
}

export function createTraffic () {
    makeRequest();
}

export function assertIsBlocked () {
    const res = makeRequest();
    check(res, {
        'Response is 429': (res) => res.status === 429
    })
}
