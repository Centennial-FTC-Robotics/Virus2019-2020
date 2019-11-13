package org.virus.Advanced_Paths;

public class Node {

    public enum paramType {Const, Variable, Operation, T_FUNC}
    public enum T_FUNC_TYPES {sin, cos, tan, csc, sec, cot, asin, acos, atan, sinh, cosh, tanh, csch, sech, coth, ln, log10, sgn};
    private paramType type;
    private T_FUNC_TYPES T_FUNC_TYPE;

    private Node parent = null;
    private Node child1 = null;
    private Node child2 = null;
    private int level;

    private String parameter;

    public Node(paramType numType, String val) {

        type = numType;
        parameter = val;
        level = 0;

        if (numType.equals(paramType.T_FUNC)) {
            T_FUNC_TYPE = T_FUNC_TYPES.valueOf(val.toLowerCase());
        } else {
            T_FUNC_TYPE = null;
        }

        if (numType.equals(paramType.Const)) {

            parameter = String.valueOf(Double.valueOf(val));
        }
    }

    //---------- Get Methods ----------//

    public paramType getType() {

        return type;
    }

    public T_FUNC_TYPES getT_FUNC_TYPE() {

        return T_FUNC_TYPE;
    }

    public int getLevel() {

        return level;
    }

    public String getVal() {

        return parameter;
    }

    public Node getParent() {

        return parent;
    }

    public Node getChild1() {

        return child1;
    }

    public Node getChild2() {

        return child2;
    }

    public Node loneClone() {

        return new Node(type, parameter);
    }

    public Node linkedClone() {

        Node clone = new Node(type, parameter);
        clone.setChild1(child1);
        clone.setChild2(child2);
        clone.setParent(parent);

        return clone;
    }

    //---------- Set Methods ----------//

    protected void setParent(Node newParent) {

        if (newParent != null) {
            parent = newParent;
            level = parent.getLevel() + 1;

            updateChildLevels();
        }
    }

    protected void setChild1(Node newChild) {

        if (newChild != null) {
            child1 = newChild;
            child1.setParent(this);
        }
    }

    protected void setChild2(Node newChild) {

        if (newChild != null && type != paramType.T_FUNC) {
            child2 = newChild;
            child2.setParent(this);
        }
    }

    //---------- Tree severing ----------//

    protected void severParent(Node validParent) {

        if (parent != null && parent.equals(validParent)) {

            parent = null;
            level = 0;

            updateChildLevels();
        }
    }

    public void updateLevel() {

        if (parent != null) {
            level = parent.getLevel() + 1;
        } else {
            level = 0;
        }

        updateChildLevels();
    }

    private void updateChildLevels() {

        if (child1 != null) {

            child1.updateLevel();
        }

        if (child2 != null) {

            child2.updateLevel();
        }
    }

    //---------- Info Methods ----------//

    public boolean subTreeContains(String value) {

        if (parameter.equals(value)) {

            return true;
        } else {

            return (((child1 != null) && child1.subTreeContains(value)) || ((child2 != null) && child2.subTreeContains(value)));
        }
    }

    @Override
    public String toString() {
        String node = "{Type:" + type  + " Value: " + parameter + " Level: " + level + " {Child1: " + ((child1 == null)? "NULL":child1.getVal()) + "} {Child2: " + ((child2 == null)? "NULL":child2.getVal()) + "}" + "}";
        return node;
    }
}
