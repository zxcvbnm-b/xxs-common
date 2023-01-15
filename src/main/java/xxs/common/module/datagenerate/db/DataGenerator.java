package xxs.common.module.datagenerate.db;

public interface DataGenerator {
      void generateBefore();
      boolean generate();
      void generatePost();
      void close();
}
