package com.singy.community.util;

import com.singy.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于代替Session对象
 */
@Component
public class HostHolder {

    /**
     * ThreadLocal的作用：它可以解决多线程的数据安全问题
     * 特点：
     *      1. ThreadLocal可以为当前线程关联一个数据（可以是普通变量、对象、数组或者集合；可以像Map一样存储数据，key为当前线程，即由当前线程获取一个Map对象）
     *      2. 每一个ThreadLocal对象，只能为当前线程关联一个数据，如果要为当前线程关联多个数据，就需要使用多个ThreadLocal对象实例
     *      3. 每个ThreadLocal对象实例定义的时候，一般都是static类型
     *      4. ThreadLocal中保存的数据，在线程销毁后，由JVM虚拟机自动释放
     * 源码（主要为get和set及remove方法）：
     *      public void set(T value) {
     *          Thread t = Thread.currentThread();
     *          ThreadLocalMap map = getMap(t);
     *          if (map != null)
     *              map.set(this, value);
     *          else
     *              createMap(t, value);
     *      }
     *      public T get() {
     *          Thread t = Thread.currentThread();
     *          ThreadLocalMap map = getMap(t);
     *          if (map != null) {
     *              ThreadLocalMap.Entry e = map.getEntry(this);
     *              if (e != null) {
     *                  @SuppressWarnings("unchecked")
     *                  T result = (T)e.value;
     *                  return result;
     *              }
     *          }
     *          return setInitialValue();
     *      }
     *      public void remove() {
     *          ThreadLocalMap m = getMap(Thread.currentThread());
     *          if (m != null)
     *              m.remove(this);
     *      }
     */
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
