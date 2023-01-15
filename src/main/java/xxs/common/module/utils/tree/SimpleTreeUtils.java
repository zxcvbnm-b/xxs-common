package xxs.common.module.utils.tree;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * 1.构造树结构  1.1获取某一个子树（）
 * 2.获取某一个节点下面的所有的子节点的ID，（用户批量删除，删除一个子树）
 * */
public class SimpleTreeUtils {
    public static <T,U extends Comparable>  List<T> getTreeByList(List<T> data, String parentIdName, String idName){
        return  getTreeByList(data, parentIdName, idName,"children",null);
    }
    public static <T,U extends Comparable>  List<T> getTreeByList(List<T> data, String parentIdName, String idName,U baseNodeIdValue, Predicate<T> predicate){
        return  getTreeByList(data, parentIdName, idName,"children",baseNodeIdValue,predicate);
    }
    public static <T,U extends Comparable>  List<T> getTreeByList(List<T> data){
        return  getTreeByList(data,"parentId", "id","children",null);
    }
    /**根据集合获取结果树形结构，最好是映射到一个单独的DTO来组合,需要确保id最小的一定是根节点中的某一个
     * 1.data：数据库中的所有的数据
     * 2.parentIdName: 父节点ID属性名称
     * 3.idName：id节点属性名称
     * 4.childrenName：子树属性名称
     * 如何寻找根节点？默认parentId最小的为根节点(比如0)适合采用主键自增的方式可以使用，如果不是主键自增的方式，使用时需要传入根节点的值，不然找不到根节点
     * 5.baseNodeIdValue:根节点ID值,到底哪个是根节点标识的值
     * */
    public static <T,U extends Comparable>  List<T>  getTreeByList(List<T> data, String parentIdName, String idName,String childrenName,U baseNodeIdValue, Predicate<T> predicate){
        if(predicate!=null){
            data=data.stream().filter((item)->{
                return predicate.test(item);
            }).collect(Collectors.toList());
        }
        List<T> allData=data;
        List<T> allBaseNodes=new ArrayList<>();
            allBaseNodes = allData.stream().filter(item -> {
                Object fieldValue = getFieldValue(parentIdName, item);
                return fieldValue==baseNodeIdValue;
            }).collect(Collectors.toList());
            /*allBaseNodes 为空的话。。。*/

            if(allBaseNodes==null||allBaseNodes.isEmpty()) {
                T oneBaseNode=null;// 根节点的数据可能有多个的，同为根节点,（可能有多个相同大小的为最小的多根树）
                oneBaseNode = allData.stream().min((item, item2) -> {
                    U fieldValue = (U) getFieldValue(parentIdName, item);
                    U fieldValue1 = (U) getFieldValue(parentIdName, item2);
                    return fieldValue.compareTo(fieldValue1);
                }).get();

                Object oneBaseNodeParentValue=getFieldValue(parentIdName, oneBaseNode);
                allBaseNodes = allData.stream().filter(item -> {
                    Object fieldValue = getFieldValue(parentIdName, item);
                    if(fieldValue==null&&oneBaseNodeParentValue==null){
                        return false;
                    }
                    return oneBaseNodeParentValue.equals(fieldValue);
                }).collect(Collectors.toList());
            }
            recursion(allData,allBaseNodes,parentIdName,idName,childrenName);

        return allBaseNodes;
    }
    private static   <T,U extends Comparable>  void recursion(List<T> allData,List<T> baseNodes,String parentIdName,String idName,String childrenName){
        for (T item:baseNodes) {
            List<T> collect = allData.stream().filter(t -> {
                    U parentValue = (U) getFieldValue(parentIdName, t);
                    U idValue = (U) getFieldValue(idName, item);
                    return  idValue.equals(parentValue);
            }).collect(Collectors.toList());
            if(!collect.isEmpty()){
                setFieldValue(childrenName,item,collect);
                recursion(allData,collect,parentIdName,idName,childrenName);
            }

        }
    }
    /**
     * 扩展：如果是id在父类的情况下，使用setXxxx(value)方法即可，因为反射拿不到父类私有的属性值，也可以使用递归获取父类的属性
     * 根据属性名称给对象设置值*/
    public static void setFieldValue(String proName,Object object,Object proValue)  {
        Class<?> aClass = object.getClass();
        Field declaredField =null;
        try {
            declaredField = aClass.getDeclaredField(proName);
            declaredField.setAccessible(true);
            declaredField.set(object,proValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                Method method = aClass.getMethod("set"+ StringUtils.capitalize(proName),Object.class);
                method.invoke(object,proValue);
            } catch (NoSuchMethodException noSuchMethodException) {
                noSuchMethodException.printStackTrace();
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            } catch (InvocationTargetException invocationTargetException) {
                invocationTargetException.printStackTrace();
            }
            e.printStackTrace();
            e.printStackTrace();
        }
    }

    /**
     * 根据属性名称获取字段值*/
    public  static Object getFieldValue(String proName,Object object)  {
        Class<?> aClass = object.getClass();
        Field declaredField =null;
        Object result =null;
        try {
            declaredField = aClass.getDeclaredField(proName);
            declaredField.setAccessible(true);
            result= declaredField.get(object);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                Method method = aClass.getMethod("get"+ StringUtils.capitalize(proName));
              return  method.invoke(object);
            } catch (NoSuchMethodException noSuchMethodException) {
                noSuchMethodException.printStackTrace();
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            } catch (InvocationTargetException invocationTargetException) {
                invocationTargetException.printStackTrace();
            }
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 2.根据某一个id获取该子树的所有id,方便删除子树，其实子树不删除也是可以的，就是数据会保存在数据库中又没有用而已
     * */
    public  static <T,U extends Comparable> List<U> getChildrenTreeIds(List<T> allData,String idProName,U id){
        return getChildrenTreeIds(allData,idProName,"parentId",id);
    }
    /**
     * 2.根据某一个id获取该子树的所有id
     * */
    public  static <T,U extends Comparable> List<U> getChildrenTreeIds(List<T> allData,U id){
        return getChildrenTreeIds(allData,"id","parentId",id);
    }
    /**
     * 2.获取子树的所有id
     * */
    public  static <T,U extends Comparable> List<U> getChildrenTreeIds(List<T> allData,String idProName,String parentIdProName,U id){
        List<U> ids=new ArrayList<>();
        ids.add(id);
        List<T> baseNodes = allData.stream().filter(item -> {
            U parentIdProValue = (U) getFieldValue(parentIdProName, item);
            return id.equals(parentIdProValue);
        }).collect(Collectors.toList());
        for (T item: baseNodes) {
            U idProValue = (U) getFieldValue(idProName, item);
            ids.add(idProValue);
        }
        recursionGetChildrenTreeIds(allData,baseNodes,idProName,parentIdProName,ids);
        return ids;
    }
     /*递归获取子节点的Id数据*/
    private static <T,U extends Comparable> void recursionGetChildrenTreeIds(List<T> allData, List<T> baseNodes, String idProName, String parentIdProName,List<U> ids) {
       if(baseNodes!=null){
           for (T item: baseNodes) {
               List<T> nodes = allData.stream().filter(i -> {
                   U parentIdProValue = (U) getFieldValue(parentIdProName, i);
                   U idProNameValue = (U) getFieldValue(idProName, item);
                   return idProNameValue.equals(parentIdProValue);
               }).collect(Collectors.toList());

               if(nodes!=null){
                   List<U> collect = nodes.stream().map(it -> {
                       U idProNameValue = (U) getFieldValue(idProName, it);
                       return idProNameValue;
                   }).collect(Collectors.toList());

                   ids.addAll(collect);
                   recursionGetChildrenTreeIds(allData,nodes,idProName,parentIdProName,ids);
               }
           }
       }
    }
/*Error:(191, 5) java: 此处不允许使用修饰符static  成员内部类不能用@Builder，是因为内部类不能有静态的属性和方法 */
    @Data
    @Builder
  static  class TestNode {
        private Long id;
        private Long parentId;
        private String name;
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY )
        private List<TestNode> children;
    }

    public static void main(String[] args) throws JsonProcessingException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<TestNode> lists=new  ArrayList<>();
        lists.add( TestNode.builder().id(1l).name("测试1").parentId(null).build());
        lists.add( TestNode.builder().id(2l).name("测试2").parentId(null).build());
        lists.add( TestNode.builder().id(3l).name("测试3").parentId(1l).build());
        lists.add( TestNode.builder().id(4l).name("测试4").parentId(2l).build());
        lists.add( TestNode.builder().id(5l).name("测试5").parentId(3l).build());
        lists.add( TestNode.builder().id(6l).name("测试6").parentId(4l).build());
        List<TestNode> treeByList = getTreeByList(lists, "parentId", "id", null,(item)->{
            System.out.println( (Long)getFieldValue("id",item)<4l);
            return (Long)getFieldValue("id",item)<=4l;
        });


    }
}
