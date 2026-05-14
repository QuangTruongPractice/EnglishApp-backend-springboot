import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    scenarios: {
        cloud_stress_test: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '20s', target: 50 }, // Ramp-up to 50 users
                { duration: '30s', target: 50 }, // Hold 50 users
                { duration: '10s', target: 0 },  // Ramp-down
            ],
            gracefulRampDown: '5s',
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<3000'], // cloud latency expected to be higher
    },
};

export default function () {
    const params = {
        redirects: 0, // don't follow redirects to avoid redirect loops
    };

    // 1. Test Learning Service
    const resLearning = http.get('https://englishapp-learning-truong-bgejhqcmd6fdgabt.southeastasia-01.azurewebsites.net/learning/api/secure/main-topics', params);
    
    // 2. Test Identity Service
    const resIdentity = http.get('https://englishapp-identity-truong-d7eeb7gmfffggzhz.southeastasia-01.azurewebsites.net/identity/api', params);
    
    // Check if the servers respond (even with 302, 401, or 403, it means the server is up and handling load)
    check(resLearning, {
        'Learning Service responded': (r) => r.status !== 0 && r.status < 500,
    });
    
    check(resIdentity, {
        'Identity Service responded': (r) => r.status !== 0 && r.status < 500,
    });
    
    sleep(Math.random() * 0.5 + 0.1);
}
