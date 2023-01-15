package xxs.common.module.utils.tree.ext1;

@FunctionalInterface
public interface Consumer<T1,T2> {

    void accept(T1 t1,T2 t2);

}