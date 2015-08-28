package walker.easydb.criterion;

/**
 * Negates another criterion
 * 
 * @author HuQingmiao
 */
public class NotExpression extends Criteria {

	private Criteria criterion;

	protected NotExpression(Criteria criterion) {
		this.criterion = criterion;
	}

	public String toSqlString() {
		return " NOT " + criterion.toSqlString();
	}

}
