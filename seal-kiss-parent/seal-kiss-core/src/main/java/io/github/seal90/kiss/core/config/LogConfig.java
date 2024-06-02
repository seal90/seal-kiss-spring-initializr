package io.github.seal90.kiss.core.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.seal90.kiss.core.log.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Import(MonitorLogAspect.class)
@Configuration
public class LogConfig {

    @Value("${seal.kiss.json.gson.ignoreTypes:jakarta.servlet.ServletResponse,jakarta.servlet.ServletRequest,org.springframework.core.io.InputStreamSource}")
    private String[] gsonIgnoreTypes;

    @Value("${seal.kiss.log.metric.useJson:true}")
    private Boolean metricJsonFlag;

    @Bean
    @ConditionalOnMissingBean(LazyToString.class)
    public LazyToString lazyToStringJson(@Qualifier("filterTypeGson") Gson gson) {
        return new LazyToStringJson(gson);
    }

    @Bean
    @ConditionalOnMissingBean(MonitorLogTemplate.class)
    public MonitorLogTemplate monitorLogTemplate(@Qualifier("filterTypeGson") Gson gson) {
        return new MonitorLogTemplateImpl(gson, metricJsonFlag);
    }

    @Bean(name = "filterTypeGson")
    public Gson filterTypeGson() {
        GsonBuilder builder = new GsonBuilder();
        if(null != gsonIgnoreTypes) {
            for (String clazzName : gsonIgnoreTypes) {
                try {
                    Class clazz = Class.forName(clazzName);
                    // 注册忽略接口类型
                    builder.registerTypeHierarchyAdapter(clazz, new ClassTypeAdapter());
                }catch (ClassNotFoundException e){
                    log.error("构建 MethodLog 过滤类型时，包含不存在的类: {}", clazzName);
                    throw new RuntimeException(e);
                }
            }
        }
        builder.registerTypeAdapter(LocalTime.class,new LocalTimeTypeAdapter());
        builder.registerTypeAdapter(LocalDate.class,new LocalDateTypeAdapter());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        builder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        return builder.create();
    }

    private static class LocalTimeTypeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {
        @Override
        public LocalTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String ldtString = jsonElement.getAsString();
            return LocalTime.parse(ldtString, DateTimeFormatter.ISO_LOCAL_TIME);
        }

        @Override
        public JsonElement serialize(LocalTime localTime, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(localTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
        }
    }

    private static class LocalDateTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String ldtString = jsonElement.getAsString();
            return LocalDate.parse(ldtString, DateTimeFormatter.ISO_LOCAL_DATE);
        }

        @Override
        public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
    }

    private static class LocalDateTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String ldtString = jsonElement.getAsString();
            return LocalDateTime.parse(ldtString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }

    private static class DurationTypeAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {

        @Override
        public Duration deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String durationString = jsonElement.getAsString();
            return Duration.parse(durationString);
        }

        @Override
        public JsonElement serialize(Duration duration, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(duration.toString());
        }
    }

    /**
     * 过滤掉的类型打印处理
     * @param <T>
     */
    private static class ClassTypeAdapter<T> extends TypeAdapter<T> {

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            // 过滤的类型输出 {}
            out.jsonValue("{}");
        }

        @Override
        public T read(JsonReader in) throws IOException {
            return null;
        }
    }

}
