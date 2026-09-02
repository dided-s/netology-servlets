package gs.konick.logger;

public enum Level {
    INFO("INFO"),
    WARN("WARN"),
    ERROR("ERROR");

    private final String level;

    Level(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return level;
    }
}
