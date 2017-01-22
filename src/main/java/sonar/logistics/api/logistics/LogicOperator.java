package sonar.logistics.api.logistics;

import java.util.ArrayList;

import com.google.common.collect.Lists;

public enum LogicOperator {
	EQUALS("=="), //
	GREATER_THAN(">"), //
	LESS_THAN("<"), //
	NOT_EQUALS("!="), //
	GREATER_THAN_OR_EQUAL(">="), //
	LESS_THAN_OR_EQUAL("<="),
	
	MATCHING_MODID("MODID"),
	MATCHING_NBT("NBT");

	public static final ArrayList<LogicOperator> numOperators = Lists.newArrayList(EQUALS, GREATER_THAN, LESS_THAN, NOT_EQUALS, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL);
	public static final ArrayList<LogicOperator> switchOperators = Lists.newArrayList(EQUALS, NOT_EQUALS);

	public String operation;

	LogicOperator(String operation) {
		this.operation = operation;
	}

	public boolean basicComparison(double num1, double num2) {
		switch (this) {
		case EQUALS:
			return num1 == num2;
		case GREATER_THAN:
			return num1 > num2;
		case LESS_THAN:
			return num1 < num2;
		case NOT_EQUALS:
			return num1 != num2;
		case GREATER_THAN_OR_EQUAL:
			return num1 >= num2;
		case LESS_THAN_OR_EQUAL:
			return num1 <= num2;
		default:
			break;
		}
		return false;

	}
}
