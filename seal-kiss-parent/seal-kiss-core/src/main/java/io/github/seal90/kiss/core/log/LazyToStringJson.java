package io.github.seal90.kiss.core.log;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 延迟序列化 JSON 格式实现
 */
@Slf4j
public class LazyToStringJson implements LazyToString {

    private Gson gson;


    public LazyToStringJson(Gson gson) {
        this.gson = gson;
    }

    @Override
    public LazyToString obj(Object object) {
        LazyToStringJsonInner inner = new LazyToStringJsonInner();
        inner.setGson(this.gson);
        inner.setObject(object);
        return inner;
    }


    @Data
    private static final class LazyToStringJsonInner implements LazyToString {

        private Gson gson;

        private Object object;

        @Override
        public LazyToString obj(Object object) {
            return null;
        }

        @Override
        public String toString() {
            return gson.toJson(object);
        }
    }
}
