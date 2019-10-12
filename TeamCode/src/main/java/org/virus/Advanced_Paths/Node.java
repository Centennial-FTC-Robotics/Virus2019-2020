package org.virus.Advanced_Paths;

public class Node {

    public enum paramType {Const, Variable, Operation}
    private paramType type;

    private Node parent = null;
    private Node child1 = null;
    private Node child2 = null;
    private int level;

    private String parameter;

    public Node(paramType numType, String val) {

        type = numType;
        parameter = val;
        level = 0;
    }

    //---------- Get Methods ----------//

    public paramType getType() {

        return type;
    }

    public int getLevel() {

        return level;
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

        if (newChild != null) {
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

        level = parent.getLevel() + 1;

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
}
