package org.alext.jitbug;


public enum Contribution {
    LINEAR_WITH_VOD("Linear With VOD", null, (byte) 0),
    LINEAR("Linear", 1, (byte) 1),
    VOD("VOD", 2, (byte) 2);

    private Integer type = null;
    private Byte id = null;
    private String alias = null;

    Contribution(String alias, Integer type, byte id) {
        this.alias = alias;
        this.type = type;
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public Byte getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }
}
