package xxs.common.module.utils.other;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArrayToStringUtils {
     private final static String DEFAULT_DELIMITER=";";
    public  static <T> String enumArrayToString(List<T>list, String delimiter){
        String result=null;
        if(list!=null&&!list.isEmpty()){
            List<String> collect =list.stream().map(item -> {
                return item.toString();
            }).sorted().collect(Collectors.toList());/*排序是为了更好的比较*/
            StringJoiner joiner = new StringJoiner(delimiter);
            collect.forEach(joiner::add);
            result=joiner.toString();
        }
        return  result;
    }
    public  static <T> String enumArrayToString(List<T>list){
       return enumArrayToString(list,DEFAULT_DELIMITER);
    }
    public  static <T> String arrayToString(List<T>list, Function<T,String> function, String delimiter){
        String result=null;
        if(list!=null&&!list.isEmpty()){
            List<String> collect =list.stream().map(item -> {
                return  function.apply(item);
            }).sorted().collect(Collectors.toList());
            StringJoiner joiner = new StringJoiner(delimiter);
            collect.forEach(joiner::add);
            result=joiner.toString();
        }
        return  result;
    }
    public  static   <T extends Enum<T>> List<T> enumStringToArray (String sourceString, String delimiter,Class<T> type){

        List<T> result=new ArrayList<>();
          if(StringUtils.isNotBlank(sourceString)){
              String[] split = sourceString.split(delimiter);
              for (int i = 0; i < split.length; i++) {
                  result.add(Enum.valueOf(type,split[i]));
              }
          }
        return  result;
    }
    public  static   <T extends Enum<T>> List<T> enumStringToArray (String sourceString,Class<T> type){
        return  enumStringToArray(sourceString,DEFAULT_DELIMITER,type);
    }
}
