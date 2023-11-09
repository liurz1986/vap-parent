package com.vrv.rule.source.datasourceconnector.es.util;



import com.vrv.rule.util.ArrayUtil;
import com.vrv.rule.util.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class QueryCondition_ES implements Serializable {

	private static final long serialVersionUID = 1L;

	private CompareExpression compareExpression = CompareExpression.Eq;

	// / <summary>
	// / 字段
	// / </summary>
	private String field;
	
	private Class<?> fieldClazz;

	// / <summary>
	// / 比较值1
	// / </summary>
	private Object value1;

	// / <summary>
	// / 比较值2
	// / </summary>
	private Object value2;
	
	/**
	 * 是否是数字类型
	 * 影响toString方法
	 */
	private boolean numFlag = false;
	
	private String fieldStr;

	private QueryCondition_ES(CompareExpression compare, String field, Object value1) {
		this(compare, field, value1, "");
	}
	
	private QueryCondition_ES(CompareExpression compare, String field, Object value1, String fieldStr) {
		this(compare, field, value1, "", fieldStr);
	}

	private QueryCondition_ES(CompareExpression compare, String field, Object value1, Object value2) {
		this.compareExpression = compare;
		this.field = field;
		this.value1 = value1;
		this.value2 = value2;
	}
	
	public QueryCondition_ES(CompareExpression compare, String field, Object value1, Object value2, String fieldStr) {
		this.compareExpression = compare;
		this.field = field;
		this.value1 = value1;
		this.value2 = value2;
		this.fieldStr = fieldStr;
	}
	
	/**
	 * 专门用作toString方法用的
	 * @param fieldStr
	 * @return
	 */
	public QueryCondition_ES aliasField(String fieldStr){
		this.fieldStr = fieldStr;
		
		return this;
	}
	
	public QueryCondition_ES copy(){
		QueryCondition_ES nCondition = new QueryCondition_ES(this.compareExpression, this.field, this.value1, this.value2);
		nCondition.numFlag = this.numFlag;
		
		return nCondition;
	}

	public static QueryCondition_ES eq(String field, Object value) {
		return new QueryCondition_ES(CompareExpression.Eq, field, value);
	}
	
	public static QueryCondition_ES eq(String field, Object value, boolean isNum) {
		QueryCondition_ES QueryCondition_ES = new QueryCondition_ES(CompareExpression.Eq, field, value);
		QueryCondition_ES.setNumFlag(isNum);
		return QueryCondition_ES;
	}

	public static QueryCondition_ES notEq(String field, Object value) {
		return new QueryCondition_ES(CompareExpression.NotEq, field, value);
	}
	
	public static QueryCondition_ES notEq(String field, Object value, boolean isNum) {
		QueryCondition_ES QueryCondition_ES = new QueryCondition_ES(CompareExpression.NotEq, field, value);
		QueryCondition_ES.setNumFlag(isNum);
		return QueryCondition_ES;
	}
	
	public static QueryCondition_ES in(String field, Collection<?> values) {
		Object[] objs = values.toArray();
		QueryCondition_ES QueryCondition_ES = new QueryCondition_ES(CompareExpression.In, field, values);
		return QueryCondition_ES;
	}

	public static QueryCondition_ES in(String field, Collection<?> values, boolean isNum) {
		Object[] objs = values.toArray();
		QueryCondition_ES QueryCondition_ES = new QueryCondition_ES(CompareExpression.In, field, objs);
		QueryCondition_ES.setNumFlag(isNum);
		return QueryCondition_ES;
	}

	public static QueryCondition_ES in(String field, String[] values) {
		return new QueryCondition_ES(CompareExpression.In, field, values);
	}
	
	public static QueryCondition_ES in(String field, String[] values, boolean isNum) {
		QueryCondition_ES QueryCondition_ES = new QueryCondition_ES(CompareExpression.In, field, values);
		QueryCondition_ES.setNumFlag(isNum);
		return QueryCondition_ES;
	}

	public static <Y extends Comparable<? super Y>> QueryCondition_ES between(String field, Y value1, Y value2) {
		Class<? extends Object> class1 = value1.getClass();
		QueryCondition_ES QueryCondition_ES = new QueryCondition_ES(CompareExpression.Between, field, value1, value2);
		QueryCondition_ES.setFieldClazz(class1);
		
		return QueryCondition_ES;
	}
	
	public static <Y extends Comparable<? super Y>> QueryCondition_ES between(String field, Y value1, Y value2, boolean isNum) {
		Class<? extends Object> class1 = value1.getClass();
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Between, field, value1, value2);
		QueryCondition_ES.setNumFlag(isNum);
		QueryCondition_ES.setFieldClazz(class1);
		return QueryCondition_ES;
	}

	public static QueryCondition_ES notNull(String field) {
		return new QueryCondition_ES(CompareExpression.NotNull, field, "");
	}

	public static QueryCondition_ES isNull(String field) {
		return new QueryCondition_ES(CompareExpression.IsNull, field, "");
	}
	
	public static QueryCondition_ES like(String field, String str) {
		return new QueryCondition_ES(CompareExpression.Like, field, str);
	}

	public static QueryCondition_ES likeEnd(String field, String str) {
		return new QueryCondition_ES(CompareExpression.LikeEnd, field, str);
	}

	public static QueryCondition_ES likeBegin(String field, String str) {
		return new QueryCondition_ES(CompareExpression.LikeBegin, field, str);
	}

	public static QueryCondition_ES gt(String field, long value) {
		QueryCondition_ES QueryCondition_ES = new QueryCondition_ES(CompareExpression.Gt, field, value);
		QueryCondition_ES.setNumFlag(true);
		
		return QueryCondition_ES;
	}

	public static QueryCondition_ES ge(String field, long value) {
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Ge, field, value);
		QueryCondition_ES.setNumFlag(true);
		
		return QueryCondition_ES;
	}

	public static QueryCondition_ES le(String field, long value) {
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Le, field, value);
		QueryCondition_ES.setNumFlag(true);
		
		return QueryCondition_ES;
	}

	public static QueryCondition_ES lt(String field, long value) {
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Lt, field, value);
		QueryCondition_ES.setNumFlag(true);
		
		return QueryCondition_ES;
	}

	public static QueryCondition_ES gt(String field, int value) {
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Gt, field, value);
		QueryCondition_ES.setNumFlag(true);
		
		return QueryCondition_ES;
	}
	
	public static QueryCondition_ES gt(String field, Date value)
	{
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Gt, field, value);
		QueryCondition_ES.setNumFlag(false);
		
		return QueryCondition_ES;
	}
	public static QueryCondition_ES gt(String field, String value)
	{
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Gt, field, value);
		QueryCondition_ES.setNumFlag(false);
		
		return QueryCondition_ES;
	}
	public static QueryCondition_ES ge(String field, Date value) {
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Ge, field, value);
		QueryCondition_ES.setNumFlag(false);
		
		return QueryCondition_ES;
	}
	
	public static QueryCondition_ES ge(String field, String value) {
		QueryCondition_ES QueryCondition_ES = new QueryCondition_ES(CompareExpression.Ge, field, value);
		QueryCondition_ES.setNumFlag(false);
		
		return QueryCondition_ES;
	}

	public static QueryCondition_ES ge(String field, int value) {
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Ge, field, value);
		QueryCondition_ES.setNumFlag(true);
		
		return QueryCondition_ES;
	}

	public static QueryCondition_ES le(String field, String value) {
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Le, field, value);
		QueryCondition_ES.setNumFlag(false);
		
		return QueryCondition_ES;
	}
	
	public static QueryCondition_ES le(String field, int value) {
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Le, field, value);
		QueryCondition_ES.setNumFlag(true);
		
		return QueryCondition_ES;
	}

	public static QueryCondition_ES lt(String field, int value) {
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Lt, field, value);
		QueryCondition_ES.setNumFlag(true);
		
		return QueryCondition_ES;
	}
	
	public static QueryCondition_ES le(String field, Date value) {
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Le, field, value);
		QueryCondition_ES.setNumFlag(false);
		
		return QueryCondition_ES;
	}

	public static QueryCondition_ES lt(String field, Date value) {
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Lt, field, value);
		QueryCondition_ES.setNumFlag(false);
		
		return QueryCondition_ES;
	}

	public static QueryCondition_ES lt(String field, BigDecimal value) {
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Lt, field, value);
		QueryCondition_ES.setNumFlag(true);
		
		return QueryCondition_ES;
	}

	public static QueryCondition_ES gt(String field, BigDecimal value) {
		QueryCondition_ES QueryCondition_ES =  new QueryCondition_ES(CompareExpression.Gt, field, value);
		QueryCondition_ES.setNumFlag(true);
		
		return QueryCondition_ES;
	}

	public static QueryCondition_ES and(QueryCondition_ES condition1, QueryCondition_ES condition2) {
		return new QueryCondition_ES(CompareExpression.And, "", condition1, condition2);
	}

	public static QueryCondition_ES and(QueryCondition_ES condition1, QueryCondition_ES condition2, QueryCondition_ES... conditions) {
		QueryCondition_ES condition = new QueryCondition_ES(CompareExpression.And, "", condition1, condition2);
		for (int i = 0; i < conditions.length; i++) {
			condition = new QueryCondition_ES(CompareExpression.And, "", condition, conditions[i]);
		}

		return condition;
	}

	public static QueryCondition_ES or(QueryCondition_ES condition1, QueryCondition_ES condition2, QueryCondition_ES... conditions) {

		QueryCondition_ES condition = new QueryCondition_ES(CompareExpression.Or, "", condition1, condition2);
		for (int i = 0; i < conditions.length; i++) {
			condition = new QueryCondition_ES(CompareExpression.Or, "", condition, conditions[i]);
		}

		return condition;
	}
	
	public static QueryCondition_ES or(List<QueryCondition_ES> cons) {
		int size = cons.size();
		if (size < 2) {
			return cons.get(0);
		}
		QueryCondition_ES condition = new QueryCondition_ES(CompareExpression.Or, "", cons.get(0), cons.get(1));
		for (int i = 2; i < size; i++) {
			condition = new QueryCondition_ES(CompareExpression.Or, "", condition, cons.get(i));
		}

		return condition;
	}

	public static QueryCondition_ES not(QueryCondition_ES condition) {
		return new QueryCondition_ES(CompareExpression.Not, "", condition);
	}

	public CompareExpression getCompareExpression() {
		return compareExpression;
	}

	public void setCompareExpression(CompareExpression compareExpression) {
		this.compareExpression = compareExpression;
	}

	public String getField() {
		return field;
	}
	
	public String getFieldStr(){
		if(StringUtils.isEmpty(fieldStr)){
			return field;
		}
		
		return fieldStr;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Object getValue1() {
		return value1;
	}

	public void setValue1(Object value1) {
		this.value1 = value1;
	}

	public Object getValue2() {
		return value2;
	}

	public void setValue2(Object value2) {
		this.value2 = value2;
	}

	@Override
	public String toString() {
		switch (getCompareExpression()) {
		case Eq:
			return eqStr();
		case NotEq:
			return notEqStr();
		case Between:
			return betweenStr();
		case In:
			return inStr();
			
		case Like:
			return getFieldStr() + " LIKE '%" + getValue1().toString() + "%'";
		case LikeBegin:
			return getFieldStr() + " LIKE '" + getValue1().toString() + "%'";
		case LikeEnd:
			return getFieldStr() + " LIKE '%" + getValue1().toString() + "'";
		case Le:
			return leStr();
		case Lt:
			return ltStr();
		case Ge:
			return geStr();
		case Gt:
			return gtStr();
		case IsNull:
			return getFieldStr() + " IS NULL";
		case NotNull:
			return getFieldStr() + " IS NOT NULL";
		case And:
			return andStr();
		case Or:
			return orStr();
		case Not:
			return notStr();
		default:
			return "";
		}
	}

	private String notStr() {
		QueryCondition_ES condition5 = (QueryCondition_ES) getValue1();
		return "(NOT (" + condition5.toString() + "))";
	}

	private String orStr() {
		QueryCondition_ES condition3 = (QueryCondition_ES) getValue1();
		QueryCondition_ES condition4 = (QueryCondition_ES) getValue2();
		return "((" + condition3.toString() + ") OR (" + condition4.toString() + "))";
	}

	private String andStr() {
		QueryCondition_ES condition1 = (QueryCondition_ES) getValue1();
		QueryCondition_ES condition2 = (QueryCondition_ES) getValue2();
		return "((" + condition1.toString() + ") AND (" + condition2.toString() + "))";
	}

	private String gtStr() {
		if(numFlag){
			return getFieldStr() + ">" + getValue1().toString() + "";
		} else{
			return getFieldStr() + ">'" + getValue1().toString() + "'";
		}
	}

	private String geStr() {
		if(numFlag){
			return getFieldStr() + ">=" + getValue1().toString() + "";
		} else{
			return getFieldStr() + ">='" + getValue1().toString() + "'";
		}
	}

	private String ltStr() {
		if(numFlag){
			return getFieldStr() + "<" + getValue1().toString() + "";
		} else{
			return getFieldStr() + "<'" + getValue1().toString() + "'";
		}
	}

	private String leStr() {
		if(numFlag){
			return getFieldStr() + "<=" + getValue1().toString() + "";
		} else{
			return getFieldStr() + "<='" + getValue1().toString() + "'";
		}
	}

	private String inStr() {
		Object[] objArray = (Object[]) getValue1();
		if (numFlag) {
			
			String join = ArrayUtil.join(objArray, ",");
			return getFieldStr() + " IN (" + join + ")";
		} else {
			String join = ArrayUtil.join(objArray, "','");
			return getFieldStr() + " IN ('" + join + "')";
		}
	}

	private String betweenStr() {
		if (getValue1() instanceof Date) {
			String dataStr1 = DateUtil.format((Date)getValue1());
			String dataStr2 = DateUtil.format((Date) getValue2());
			return getFieldStr() + " BETWEEN '" + dataStr1 + "' AND '" + dataStr2 + "'";
		} else if(this.numFlag){
			return getFieldStr() + " BETWEEN " + getValue1().toString() + " AND " + getValue2().toString() + "";
		}else{
			return getFieldStr() + " BETWEEN '" + getValue1().toString() + "' AND '" + getValue2().toString() + "'";
		}
	}

	private String notEqStr() {
		if(this.numFlag){
			return getFieldStr() + "<>" + getValue1().toString() + "";
		}else{
			return getFieldStr() + "<>'" + getValue1().toString() + "'";
		}
	}

	private String eqStr() {
		if(this.numFlag){
			return getFieldStr() + "=" + getValue1().toString() + "";
		} else{
			return getFieldStr() + "='" + getValue1().toString() + "'";
		}
	}

	public boolean isNumFlag() {
		return numFlag;
	}

	public void setNumFlag(boolean numFlag) {
		this.numFlag = numFlag;
	}

	public Class<?> getFieldClazz() {
		return fieldClazz;
	}

	public void setFieldClazz(Class<?> fieldClazz) {
		this.fieldClazz = fieldClazz;
	}
}
