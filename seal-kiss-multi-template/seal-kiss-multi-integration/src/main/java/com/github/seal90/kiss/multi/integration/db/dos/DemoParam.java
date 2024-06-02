package com.github.seal90.kiss.multi.integration.db.dos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DemoParam {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DemoParam() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andStringAttrIsNull() {
            addCriterion("string_attr is null");
            return (Criteria) this;
        }

        public Criteria andStringAttrIsNotNull() {
            addCriterion("string_attr is not null");
            return (Criteria) this;
        }

        public Criteria andStringAttrEqualTo(String value) {
            addCriterion("string_attr =", value, "stringAttr");
            return (Criteria) this;
        }

        public Criteria andStringAttrNotEqualTo(String value) {
            addCriterion("string_attr <>", value, "stringAttr");
            return (Criteria) this;
        }

        public Criteria andStringAttrGreaterThan(String value) {
            addCriterion("string_attr >", value, "stringAttr");
            return (Criteria) this;
        }

        public Criteria andStringAttrGreaterThanOrEqualTo(String value) {
            addCriterion("string_attr >=", value, "stringAttr");
            return (Criteria) this;
        }

        public Criteria andStringAttrLessThan(String value) {
            addCriterion("string_attr <", value, "stringAttr");
            return (Criteria) this;
        }

        public Criteria andStringAttrLessThanOrEqualTo(String value) {
            addCriterion("string_attr <=", value, "stringAttr");
            return (Criteria) this;
        }

        public Criteria andStringAttrLike(String value) {
            addCriterion("string_attr like", value, "stringAttr");
            return (Criteria) this;
        }

        public Criteria andStringAttrNotLike(String value) {
            addCriterion("string_attr not like", value, "stringAttr");
            return (Criteria) this;
        }

        public Criteria andStringAttrIn(List<String> values) {
            addCriterion("string_attr in", values, "stringAttr");
            return (Criteria) this;
        }

        public Criteria andStringAttrNotIn(List<String> values) {
            addCriterion("string_attr not in", values, "stringAttr");
            return (Criteria) this;
        }

        public Criteria andStringAttrBetween(String value1, String value2) {
            addCriterion("string_attr between", value1, value2, "stringAttr");
            return (Criteria) this;
        }

        public Criteria andStringAttrNotBetween(String value1, String value2) {
            addCriterion("string_attr not between", value1, value2, "stringAttr");
            return (Criteria) this;
        }

        public Criteria andIntegerAttrIsNull() {
            addCriterion("integer_attr is null");
            return (Criteria) this;
        }

        public Criteria andIntegerAttrIsNotNull() {
            addCriterion("integer_attr is not null");
            return (Criteria) this;
        }

        public Criteria andIntegerAttrEqualTo(Integer value) {
            addCriterion("integer_attr =", value, "integerAttr");
            return (Criteria) this;
        }

        public Criteria andIntegerAttrNotEqualTo(Integer value) {
            addCriterion("integer_attr <>", value, "integerAttr");
            return (Criteria) this;
        }

        public Criteria andIntegerAttrGreaterThan(Integer value) {
            addCriterion("integer_attr >", value, "integerAttr");
            return (Criteria) this;
        }

        public Criteria andIntegerAttrGreaterThanOrEqualTo(Integer value) {
            addCriterion("integer_attr >=", value, "integerAttr");
            return (Criteria) this;
        }

        public Criteria andIntegerAttrLessThan(Integer value) {
            addCriterion("integer_attr <", value, "integerAttr");
            return (Criteria) this;
        }

        public Criteria andIntegerAttrLessThanOrEqualTo(Integer value) {
            addCriterion("integer_attr <=", value, "integerAttr");
            return (Criteria) this;
        }

        public Criteria andIntegerAttrIn(List<Integer> values) {
            addCriterion("integer_attr in", values, "integerAttr");
            return (Criteria) this;
        }

        public Criteria andIntegerAttrNotIn(List<Integer> values) {
            addCriterion("integer_attr not in", values, "integerAttr");
            return (Criteria) this;
        }

        public Criteria andIntegerAttrBetween(Integer value1, Integer value2) {
            addCriterion("integer_attr between", value1, value2, "integerAttr");
            return (Criteria) this;
        }

        public Criteria andIntegerAttrNotBetween(Integer value1, Integer value2) {
            addCriterion("integer_attr not between", value1, value2, "integerAttr");
            return (Criteria) this;
        }

        public Criteria andLongAttrIsNull() {
            addCriterion("long_attr is null");
            return (Criteria) this;
        }

        public Criteria andLongAttrIsNotNull() {
            addCriterion("long_attr is not null");
            return (Criteria) this;
        }

        public Criteria andLongAttrEqualTo(Long value) {
            addCriterion("long_attr =", value, "longAttr");
            return (Criteria) this;
        }

        public Criteria andLongAttrNotEqualTo(Long value) {
            addCriterion("long_attr <>", value, "longAttr");
            return (Criteria) this;
        }

        public Criteria andLongAttrGreaterThan(Long value) {
            addCriterion("long_attr >", value, "longAttr");
            return (Criteria) this;
        }

        public Criteria andLongAttrGreaterThanOrEqualTo(Long value) {
            addCriterion("long_attr >=", value, "longAttr");
            return (Criteria) this;
        }

        public Criteria andLongAttrLessThan(Long value) {
            addCriterion("long_attr <", value, "longAttr");
            return (Criteria) this;
        }

        public Criteria andLongAttrLessThanOrEqualTo(Long value) {
            addCriterion("long_attr <=", value, "longAttr");
            return (Criteria) this;
        }

        public Criteria andLongAttrIn(List<Long> values) {
            addCriterion("long_attr in", values, "longAttr");
            return (Criteria) this;
        }

        public Criteria andLongAttrNotIn(List<Long> values) {
            addCriterion("long_attr not in", values, "longAttr");
            return (Criteria) this;
        }

        public Criteria andLongAttrBetween(Long value1, Long value2) {
            addCriterion("long_attr between", value1, value2, "longAttr");
            return (Criteria) this;
        }

        public Criteria andLongAttrNotBetween(Long value1, Long value2) {
            addCriterion("long_attr not between", value1, value2, "longAttr");
            return (Criteria) this;
        }

        public Criteria andBigDecimalAttrIsNull() {
            addCriterion("big_decimal_attr is null");
            return (Criteria) this;
        }

        public Criteria andBigDecimalAttrIsNotNull() {
            addCriterion("big_decimal_attr is not null");
            return (Criteria) this;
        }

        public Criteria andBigDecimalAttrEqualTo(BigDecimal value) {
            addCriterion("big_decimal_attr =", value, "bigDecimalAttr");
            return (Criteria) this;
        }

        public Criteria andBigDecimalAttrNotEqualTo(BigDecimal value) {
            addCriterion("big_decimal_attr <>", value, "bigDecimalAttr");
            return (Criteria) this;
        }

        public Criteria andBigDecimalAttrGreaterThan(BigDecimal value) {
            addCriterion("big_decimal_attr >", value, "bigDecimalAttr");
            return (Criteria) this;
        }

        public Criteria andBigDecimalAttrGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("big_decimal_attr >=", value, "bigDecimalAttr");
            return (Criteria) this;
        }

        public Criteria andBigDecimalAttrLessThan(BigDecimal value) {
            addCriterion("big_decimal_attr <", value, "bigDecimalAttr");
            return (Criteria) this;
        }

        public Criteria andBigDecimalAttrLessThanOrEqualTo(BigDecimal value) {
            addCriterion("big_decimal_attr <=", value, "bigDecimalAttr");
            return (Criteria) this;
        }

        public Criteria andBigDecimalAttrIn(List<BigDecimal> values) {
            addCriterion("big_decimal_attr in", values, "bigDecimalAttr");
            return (Criteria) this;
        }

        public Criteria andBigDecimalAttrNotIn(List<BigDecimal> values) {
            addCriterion("big_decimal_attr not in", values, "bigDecimalAttr");
            return (Criteria) this;
        }

        public Criteria andBigDecimalAttrBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("big_decimal_attr between", value1, value2, "bigDecimalAttr");
            return (Criteria) this;
        }

        public Criteria andBigDecimalAttrNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("big_decimal_attr not between", value1, value2, "bigDecimalAttr");
            return (Criteria) this;
        }

        public Criteria andLocalTimeAttrIsNull() {
            addCriterion("local_time_attr is null");
            return (Criteria) this;
        }

        public Criteria andLocalTimeAttrIsNotNull() {
            addCriterion("local_time_attr is not null");
            return (Criteria) this;
        }

        public Criteria andLocalTimeAttrEqualTo(LocalTime value) {
            addCriterion("local_time_attr =", value, "localTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalTimeAttrNotEqualTo(LocalTime value) {
            addCriterion("local_time_attr <>", value, "localTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalTimeAttrGreaterThan(LocalTime value) {
            addCriterion("local_time_attr >", value, "localTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalTimeAttrGreaterThanOrEqualTo(LocalTime value) {
            addCriterion("local_time_attr >=", value, "localTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalTimeAttrLessThan(LocalTime value) {
            addCriterion("local_time_attr <", value, "localTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalTimeAttrLessThanOrEqualTo(LocalTime value) {
            addCriterion("local_time_attr <=", value, "localTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalTimeAttrIn(List<LocalTime> values) {
            addCriterion("local_time_attr in", values, "localTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalTimeAttrNotIn(List<LocalTime> values) {
            addCriterion("local_time_attr not in", values, "localTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalTimeAttrBetween(LocalTime value1, LocalTime value2) {
            addCriterion("local_time_attr between", value1, value2, "localTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalTimeAttrNotBetween(LocalTime value1, LocalTime value2) {
            addCriterion("local_time_attr not between", value1, value2, "localTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateAttrIsNull() {
            addCriterion("local_date_attr is null");
            return (Criteria) this;
        }

        public Criteria andLocalDateAttrIsNotNull() {
            addCriterion("local_date_attr is not null");
            return (Criteria) this;
        }

        public Criteria andLocalDateAttrEqualTo(LocalDate value) {
            addCriterion("local_date_attr =", value, "localDateAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateAttrNotEqualTo(LocalDate value) {
            addCriterion("local_date_attr <>", value, "localDateAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateAttrGreaterThan(LocalDate value) {
            addCriterion("local_date_attr >", value, "localDateAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateAttrGreaterThanOrEqualTo(LocalDate value) {
            addCriterion("local_date_attr >=", value, "localDateAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateAttrLessThan(LocalDate value) {
            addCriterion("local_date_attr <", value, "localDateAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateAttrLessThanOrEqualTo(LocalDate value) {
            addCriterion("local_date_attr <=", value, "localDateAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateAttrIn(List<LocalDate> values) {
            addCriterion("local_date_attr in", values, "localDateAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateAttrNotIn(List<LocalDate> values) {
            addCriterion("local_date_attr not in", values, "localDateAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateAttrBetween(LocalDate value1, LocalDate value2) {
            addCriterion("local_date_attr between", value1, value2, "localDateAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateAttrNotBetween(LocalDate value1, LocalDate value2) {
            addCriterion("local_date_attr not between", value1, value2, "localDateAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateTimeAttrIsNull() {
            addCriterion("local_date_time_attr is null");
            return (Criteria) this;
        }

        public Criteria andLocalDateTimeAttrIsNotNull() {
            addCriterion("local_date_time_attr is not null");
            return (Criteria) this;
        }

        public Criteria andLocalDateTimeAttrEqualTo(LocalDateTime value) {
            addCriterion("local_date_time_attr =", value, "localDateTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateTimeAttrNotEqualTo(LocalDateTime value) {
            addCriterion("local_date_time_attr <>", value, "localDateTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateTimeAttrGreaterThan(LocalDateTime value) {
            addCriterion("local_date_time_attr >", value, "localDateTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateTimeAttrGreaterThanOrEqualTo(LocalDateTime value) {
            addCriterion("local_date_time_attr >=", value, "localDateTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateTimeAttrLessThan(LocalDateTime value) {
            addCriterion("local_date_time_attr <", value, "localDateTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateTimeAttrLessThanOrEqualTo(LocalDateTime value) {
            addCriterion("local_date_time_attr <=", value, "localDateTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateTimeAttrIn(List<LocalDateTime> values) {
            addCriterion("local_date_time_attr in", values, "localDateTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateTimeAttrNotIn(List<LocalDateTime> values) {
            addCriterion("local_date_time_attr not in", values, "localDateTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateTimeAttrBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("local_date_time_attr between", value1, value2, "localDateTimeAttr");
            return (Criteria) this;
        }

        public Criteria andLocalDateTimeAttrNotBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("local_date_time_attr not between", value1, value2, "localDateTimeAttr");
            return (Criteria) this;
        }

        public Criteria andDurationAttrIsNull() {
            addCriterion("duration_attr is null");
            return (Criteria) this;
        }

        public Criteria andDurationAttrIsNotNull() {
            addCriterion("duration_attr is not null");
            return (Criteria) this;
        }

        public Criteria andDurationAttrEqualTo(Long value) {
            addCriterion("duration_attr =", value, "durationAttr");
            return (Criteria) this;
        }

        public Criteria andDurationAttrNotEqualTo(Long value) {
            addCriterion("duration_attr <>", value, "durationAttr");
            return (Criteria) this;
        }

        public Criteria andDurationAttrGreaterThan(Long value) {
            addCriterion("duration_attr >", value, "durationAttr");
            return (Criteria) this;
        }

        public Criteria andDurationAttrGreaterThanOrEqualTo(Long value) {
            addCriterion("duration_attr >=", value, "durationAttr");
            return (Criteria) this;
        }

        public Criteria andDurationAttrLessThan(Long value) {
            addCriterion("duration_attr <", value, "durationAttr");
            return (Criteria) this;
        }

        public Criteria andDurationAttrLessThanOrEqualTo(Long value) {
            addCriterion("duration_attr <=", value, "durationAttr");
            return (Criteria) this;
        }

        public Criteria andDurationAttrIn(List<Long> values) {
            addCriterion("duration_attr in", values, "durationAttr");
            return (Criteria) this;
        }

        public Criteria andDurationAttrNotIn(List<Long> values) {
            addCriterion("duration_attr not in", values, "durationAttr");
            return (Criteria) this;
        }

        public Criteria andDurationAttrBetween(Long value1, Long value2) {
            addCriterion("duration_attr between", value1, value2, "durationAttr");
            return (Criteria) this;
        }

        public Criteria andDurationAttrNotBetween(Long value1, Long value2) {
            addCriterion("duration_attr not between", value1, value2, "durationAttr");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}