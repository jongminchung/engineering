package io.github.jongminchung.distributedlock.provider.redis.redisson;

import java.time.Duration;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.testcontainers.containers.RedisContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import io.github.jongminchung.distributedlock.core.api.LockHandle;
import io.github.jongminchung.distributedlock.core.api.LockRequest;
import io.github.jongminchung.distributedlock.core.key.LockKey;
import io.github.jongminchung.distributedlock.provider.redis.config.RedisLockProviderConfig;

@Testcontainers
class RedissonDistributedLockTest {
    @Container
    private static final RedisContainer REDIS = new RedisContainer(DockerImageName.parse("redis:7.2-alpine"));

    @Test
    void acquiresAndReleasesLock() {
        RedisLockProviderConfig config = new RedisLockProviderConfig();
        config.setDefaultWaitTime(Duration.ofSeconds(1));
        config.setDefaultLeaseTime(Duration.ofSeconds(2));
        RedissonClient client = createClient();
        try {
            RedissonDistributedLock lock = new RedissonDistributedLock(client, config);
            LockRequest request =
                    LockRequest.of(LockKey.of("order:1"), Duration.ofSeconds(1), Duration.ofSeconds(2), false);
            LockHandle handle = lock.acquire(request);

            Assertions.assertThat(lock.tryAcquire(request)).isEmpty();
            handle.release();
            Optional<LockHandle> reacquired = lock.tryAcquire(request);
            Assertions.assertThat(reacquired).isPresent();
            reacquired.get().release();
        } finally {
            client.shutdown();
        }
    }

    private RedissonClient createClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + REDIS.getHost() + ":" + REDIS.getFirstMappedPort());
        return Redisson.create(config);
    }
}
