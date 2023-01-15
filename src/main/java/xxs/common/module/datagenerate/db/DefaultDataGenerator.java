package xxs.common.module.datagenerate.db;

import java.sql.Connection;

public class DefaultDataGenerator implements DataGenerator {
    private Connection connection;

    @Override
    public void generateBefore() {

    }

    @Override
    public boolean generate() {
        return false;
    }

    @Override
    public void generatePost() {

    }

    @Override
    public void close() {

    }
}
