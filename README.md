# `synchronized` , `volatile`, `Atomic`, `reentrant`

## synchronized
- 객체에 대한, 내재적 락(intrinsic locks)(또는 모니터락(monitor lock))을 사용한다. 락을 가진 하나의 스레드만 실행되고, 해당 synchronized 블럭에 접근한 다른 스레드는 블럭된다.
  - 객체(인스턴스)에 대한 락이기 때문에 하나의 클래스에 `@Synchronized`를 여러 메서드에 사용하면, 같은 락을 공유하게 된다.
    - Synchronized Methods
      ```kotlin
          class TestData {
            @Synchronized fun test1() {
              Thread.sleep(100)
            }

            @Synchronized fun test2() {
              Thread.sleep(100)
            }
        }
      ```
      - 위의 두 메서드를 동시 실행할 경우 같은 락에 접근한 것이기 때문에 둘 중 하나는 블럭된다.
- Synchronized Statements
  - 객체를 지정해서 synchronized 블럭을 지정할 수 있다.  `synchronized(대상) { }` 
  - 하나의 객체의 각 메서드에서 각기 다른 락을 쓸 수 있다. 
- 락을 사용하여 스레드 안전성을 보장하지만, 대기 중인 스레드를 블럭시키므로 성능이 저하될 수 있다. 

## Volatile
- 락을 사용하지 않으면서도, 동시성 처리를 하는 방법 중 하나.
- 멀티스레드 환경에서, 여러 스레드가 다른 코어에서 동시에 실행되고, 각 스레드는 각자의 메모리를 공간을 갖고 있다(캐시된 메모리). 그래서 싱크가 맞지 않는 문제가 발생한다. 한 스레드는 A 값을 썼는데, 다른 스레드는 캐시된 값에서 읽어 다른 값을 보고 있을 수 있다.
- volatile은 사용하기 전에 항상 메인 메모리에서 다시 읽는다  그리고, volatile에 대한 명령이 완료되기 전에, 스레드가 쓴 값은 메인 메모리에 쓴다
  - 즉, 스레드의 각자의 캐시에서가 아니라, 항상 메인 메모리에서 읽고 쓰기 방법으로 동시성 처리를 한다.
- 위의, 내재적 락 같이 무거운 락을 쓰지 않기 때문에 가볍다고 볼 수 있지만, 매번 메인 메모리에서 읽고 쓰기 때문에 오히려 무거운 연산이라고 할 수 있다.
- 또한, 락이 아니라, 메인 메모리에 읽고 쓰기 때문에 스레드 간에 값을 덮어쓰는 문제가 발생할 수 있다. c++ 같은 경우가 c = c + 1 이기 때문에, 읽고 나서 쓰는 사이에 다른 스레드에서 값을 증가시켜 두었을 수 있다.

## Atomic
```kotlin
 private val atomicInt = AtomicInteger(0)
        
 fun increase() =
      atomicInt.incrementAndGet()
```
- volatile과 마찬가지로, 락을 사용하지 않으면서 동시성 처리를 하는 방법 중 하나.
- CAS를 사용한다.
  - CAS란, Compare and Swap 으로 내가 읽은 값과 메모리의 값을 비교해서, 같을 때만 메모리에 쓴다.
  - CPU가 이것이 비교해서 한 번에 스왑해주기 때문에 (하나의 명령으로 되어있어) 원자적 연산이 보장된다.
- 락을 사용하지 않아 synchronized보다 빠르면서도, 연산의 원자성이 보장된다.
- 간단한 연산만 지원해, 복잡한 연산은 적합하지 않다.
- 내부를 들어가보면, while 문으로 일치할 때까지 계속해서, 시도함을 알 수 있다.  어떻게든 해당 연산을 올바르게(메모리에 쓰인 값에 대한 연산으로) 해주는 것이다. 잘못하면, 성능이 안좋아질 수 있겠다.
```java
   @IntrinsicCandidate
    public final int getAndAddInt(Object o, long offset, int delta) {
        int v;
        do {
            v = getIntVolatile(o, offset);
        } while (!weakCompareAndSetInt(o, offset, v, v + delta));
        return v;
    }
```


## ReentrantLock
- synchronized는 락을 사용한다는 것만 알지, 대기 중이던 스레드들이 어떻게 관리되고, 대기 하던 스레드 중 다음 번엔 누가 실행되는지 알기 어렵다. 
- ReentrantLock 은 이런 것들에 대한 세밀한 관리가 가능하다. ( Lock 인터페이스 구현) 
  - 예를 들어, fairness 파라미터를 true 로 하면, 가장 오래 기다린 스레드가 다음 스레드가 된다. 
  - 타임아웃을 설정할 수 있는 등 여러 기능이 있다.
- AQS(Abastract Queued Synchronizer) 을 기반으로 한다 .
  - AbstractQueuedSynchronizer 을 구현.
  - 대기큐를 기반으로 해, 이러한 세밀한 조정이 가능하다 정도로 이해했다.  
- lock 해제를 직접 해줘야 해서, 해제가 누락될 수 있고, 제어할 수 있는 것이 많아 사용이 더 복잡할 수 있다. 
