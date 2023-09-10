package xxs.common.module.codegenerate;

import cn.hutool.core.io.FileUtil;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * java类方法合并，属性合并 import
 */
public class SimpleJavaClassMerge {
    public static void main(String[] args) {
        String javaFile = "F:\\xxs-common\\src\\main\\java\\xxs\\common\\module\\codegenerate\\method\\template\\MethodAllTemplate.java";
        String string = FileUtil.readUtf8String(new File(javaFile));
        String javaFile2 = "F:\\xxs-common\\src\\main\\java\\xxs\\common\\module\\codegenerate\\method\\template\\MethodControllerTemplate.java";
        String string2 = FileUtil.readUtf8String(new File(javaFile2));
        mergeJavaSrc(string, string2);
    }

    /**
     * @param javaSrcText1 java源代码text1
     * @param javaSrcText2 java源代码text2
     * @return
     */
    public static String mergeJavaSrc(String javaSrcText1, String javaSrcText2) {
        String result;
        ClassOrInterfaceDeclaration classOrInterfaceDeclarationResult = null;
        CompilationUnit compilationUnitResult = null;
        Map<String, MethodDeclaration> javaSrc1MethodNodeMap = new HashMap<>(8);
        Map<String, MethodDeclaration> javaSrc2MethodNodeMap = new HashMap<>(8);

        Map<String, FieldDeclaration> javaSrc1FieldNodeMap = new HashMap<>(8);
        Map<String, FieldDeclaration> javaSrc2FieldNodeMap = new HashMap<>(8);

        Map<String, ImportDeclaration> javaSrc1ImportNodeMap = new HashMap<>(8);
        Map<String, ImportDeclaration> javaSrc2ImportNodeMap = new HashMap<>(8);

        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> javaSrc1ParseResult = javaParser.parse(javaSrcText1);
        if (javaSrc1ParseResult.isSuccessful()) {
            if (javaSrc1ParseResult.getResult().isPresent()) {
                compilationUnitResult = javaSrc1ParseResult.getResult().get();
                TypeDeclaration<?> type = compilationUnitResult.getType(0);
                compilationUnitResult.accept(new GenericVisitorAdapter<Object, Object>() {
                    @Override
                    public Object visit(ImportDeclaration n, Object arg) {
                        javaSrc1ImportNodeMap.put(n.getName().getId(), n);
                        return super.visit(n, arg);
                    }
                }, null);
                classOrInterfaceDeclarationResult = type.asClassOrInterfaceDeclaration();
                Node node = classOrInterfaceDeclarationResult.getParentNode().get();
                classOrInterfaceDeclarationResult.accept(new GenericVisitorAdapter() {
                    @Override
                    public Object visit(MethodDeclaration n, Object arg) {
                        String methodParam = n.getParameters().toString();
                        String methodName = n.getName().getId();
                        javaSrc1MethodNodeMap.put(methodName + methodParam, n);
                        return super.visit(n, arg);
                    }

                    @Override
                    public Object visit(FieldDeclaration n, Object arg) {
                        javaSrc1FieldNodeMap.put(n.toString(), n);
                        return super.visit(n, arg);
                    }

                }, null);
            } else {
                return javaSrcText1;
            }
        } else {
            return javaSrcText1;
        }

        ParseResult<CompilationUnit> javaSrc2ParseResult = javaParser.parse(javaSrcText2);
        if (javaSrc2ParseResult.isSuccessful()) {
            if (javaSrc2ParseResult.getResult().isPresent()) {
                CompilationUnit compilationUnit = javaSrc2ParseResult.getResult().get();
                TypeDeclaration<?> type = compilationUnit.getType(0);
                ClassOrInterfaceDeclaration classOrInterfaceDeclaration = type.asClassOrInterfaceDeclaration();
                compilationUnit.accept(new GenericVisitorAdapter<Object, Object>() {
                    @Override
                    public Object visit(ImportDeclaration n, Object arg) {
                        javaSrc2ImportNodeMap.put(n.getName().getId(), n);
                        return super.visit(n, arg);
                    }
                }, null);
                classOrInterfaceDeclaration.accept(new GenericVisitorAdapter() {
                    @Override
                    public Object visit(MethodDeclaration n, Object arg) {
                        String methodParam = n.getParameters().toString();
                        String methodName = n.getName().getId();
                        javaSrc2MethodNodeMap.put(methodName + methodParam, n);
                        return super.visit(n, arg);
                    }
                    @Override
                    public Object visit(FieldDeclaration n, Object arg) {
                        javaSrc2FieldNodeMap.put(n.toString(), n);
                        return super.visit(n, arg);
                    }
                }, null);
            }
        }
        for (Map.Entry<String, FieldDeclaration> stringNodeEntry : javaSrc2FieldNodeMap.entrySet()) {
            if (!javaSrc1FieldNodeMap.keySet().contains(stringNodeEntry.getKey())) {
                classOrInterfaceDeclarationResult.addMember(stringNodeEntry.getValue());
            }
        }
        for (Map.Entry<String, MethodDeclaration> stringNodeEntry : javaSrc2MethodNodeMap.entrySet()) {
            if (!javaSrc1MethodNodeMap.keySet().contains(stringNodeEntry.getKey())) {
                classOrInterfaceDeclarationResult.addMember(stringNodeEntry.getValue());
            }
        }
        for (Map.Entry<String, ImportDeclaration> stringNodeEntry : javaSrc2ImportNodeMap.entrySet()) {
            if (!javaSrc1ImportNodeMap.keySet().contains(stringNodeEntry.getKey())) {
                compilationUnitResult.addImport(stringNodeEntry.getValue());
            }
        }
        result = compilationUnitResult.toString();
        return result;
    }
}
