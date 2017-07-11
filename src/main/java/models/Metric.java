/**
 * Created by jyot on 11/7/17.
 */

package models;

public class Metric {
    private String objectName;
    private String attribute;
    private String type;
    private double value;

    public String getObjectName() {
        return objectName;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getType() {
        return type;
    }

    public double getValue() {
        return value;
    }
}