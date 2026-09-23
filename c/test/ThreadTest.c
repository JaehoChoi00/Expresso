#include <stdio.h>
#include <pthread.h>

#include "expresso/Exposure.h"
#include "expresso/Expresso.h"
#include "expresso/ExposureCategory.h"

#define THREAD_COUNT 4
#define EXPOSURES_PER_THREAD 100

typedef struct {
    int threadId;
    Exposure *exposure;
} ThreadData;

static void *threadFunction(void *argument) {
    ThreadData *data = (ThreadData *)argument;

    for (int i = 0; i < EXPOSURES_PER_THREAD; i++) {
        l1(data->exposure, EXPOSURE_TEST, "Thread %d - Exposure %d\n", data->threadId, i + 1);
    }

    return NULL;
}

int main(void) {
    pthread_t threads[THREAD_COUNT];
    ThreadData threadData[THREAD_COUNT];

    Exposure exposure = createWithIdentity("ThreadTest");

    printf("EXPRESSO C THREAD TEST\n");

    reset();
    setLevel(LEVEL1);
    setCategory(EXPOSURE_TEST);

    printf("Threads: %d\n", THREAD_COUNT);
    printf("Exposures per thread: %d\n", EXPOSURES_PER_THREAD);
    printf("Expected event count: %d\n\n", THREAD_COUNT * EXPOSURES_PER_THREAD);

    for (int i = 0; i < THREAD_COUNT; i++) {
        threadData[i].threadId = i + 1;
        threadData[i].exposure = &exposure;

        int result = pthread_create(&threads[i], NULL, threadFunction, &threadData[i]);

        if (result != 0) {
            printf("Failed to create thread %d\n", i + 1);
            return 1;
        }
    }

    for (int i = 0; i < THREAD_COUNT; i++) {
        int result = pthread_join(threads[i], NULL);

        if (result != 0) {
            printf("Failed to join thread %d\n", i + 1);
            return 1;
        }
    }

    printf("\n=== Results ===\n\n");

    long long expected = THREAD_COUNT * EXPOSURES_PER_THREAD;
    long long actual = getCurrentEventCount();

    printf("Expected event count: %lld\n", expected);
    printf("Actual event count:   %lld\n", actual);

    if (actual == expected) {
        printf("PASS: Event counter is thread-safe.\n");
    } else {
        printf("FAIL: Event counter mismatch.\n");
    }

    printf("THREAD TEST COMPLETE\n");

    return actual == expected ? 0 : 1;
}
