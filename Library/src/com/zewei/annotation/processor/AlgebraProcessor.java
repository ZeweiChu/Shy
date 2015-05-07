package com.zewei.annotation.processor;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes(value={"com.zewei.annotation.processor.Algebra"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class AlgebraProcessor extends AbstractProcessor {

	private Filer filer;
	
	@Override
	public void init(ProcessingEnvironment env){
		filer = env.getFiler();
	}
	
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment env) {
		String folder = null;
		String classContent = null;
		String algName;
		JavaFileObject jfo = null;
		for (Element element: env.getElementsAnnotatedWith(Algebra.class)) {			
			
			// Avoid infinite loop.
			if (element.getSimpleName().toString().startsWith("G_")) {
				continue;
			}

			// Initialization.
			TypeMirror tm = element.asType();
			String typeArgs = tm.accept(new DeclaredTypeVisitor(), element);
			String[] lTypeArgs = toList(typeArgs);
			algName = element.getSimpleName().toString();
			
			// Attention!
			// Currently the program only supports algebras with arguments of the methods to be:
			// (1) Simple types. "String / int / float / ...".
			// (2) List<R>, where R must be one of the generic types.
			
			// Create transform classes "AlgNameTransform".
			folder = "transform";
			classContent = createTransformClass(folder, element, lTypeArgs, typeArgs);
			jfo = null;
			try {
				jfo = filer.createSourceFile(folder + "/" + algName +"Transform", element);
				jfo.openWriter().append(classContent).close();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			
			folder = "util";
			classContent = createUtilClass(folder, element, lTypeArgs, typeArgs);
			jfo = null;
			try {
				jfo = filer.createSourceFile(folder + "/" + algName +"Trans", element);
				jfo.openWriter().append(classContent).close();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			
			// Create transform classes "G_AlgNameTransform".
			folder = "transform";
			classContent = createSubstTransformClass(folder, element, lTypeArgs, typeArgs);
			jfo = null;
			try {
				jfo = filer.createSourceFile(folder + "/G_" + algName +"Transform", element);
				jfo.openWriter().append(classContent).close();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			
			
		    // Create query classes "AlgNameQuery".
			// One issue here. Using "java.util.List" instead of "List".
			folder = "query";
			classContent = createQueryClass(folder, element, lTypeArgs, typeArgs);
			jfo = null;
			try{
				jfo = filer.createSourceFile(folder + "/" + algName + "Query", element);
				jfo.openWriter().append(classContent).close();
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
			
			// Create query classes "G_AlgNameQuery".
			folder = "query";
			classContent = createGeneralQueryClass(folder, element, lTypeArgs, typeArgs);
			jfo = null;
			try{
				jfo = filer.createSourceFile(folder + "/G_" + algName + "Query", element);
				jfo.openWriter().append(classContent).close();
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
			
			// Create combinator classes "CombineAlgName".
			folder = "combinator";
			classContent = createCombinatorClass(folder, element, typeArgs);
			jfo = null;
			try{
				jfo = filer.createSourceFile(folder + "/Combine" + algName, element);
				jfo.openWriter().append(classContent).close();
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
			
		}
		return true;		
	}
	
	private String[] toList(String message) {
		return message.split(",");
	}

	@Override
	public SourceVersion getSupportedSourceVersion(){
		return SourceVersion.latestSupported();
	}
	
	private String getPackage(Element element) {
		return ((PackageElement)element.getEnclosingElement()).getQualifiedName().toString();
	}
	
	String createTransformClass(String folder, Element element, String[] lTypeArgs, String typeArgs) {
		List<? extends Element> le = element.getEnclosedElements();
		String algName = element.getSimpleName().toString();
		String algNameLower = algName.substring(0, 1).toLowerCase() + algName.substring(1);
		String className = algName + "Transform";
		String classContent = "package " + folder + ";\n\n";
		String typeArguments = "<";
		for (int i = 0; i < lTypeArgs.length; i++) {
			typeArguments += "A" + i;
			if (i < lTypeArgs.length - 1) typeArguments += ", ";
		}
		typeArguments += ">";
		classContent += "import " + getPackage(element) + "." + algName + ";\n\n";
		classContent += "public interface " + className + typeArguments + " extends " + algName + typeArguments + " {\n\n";
		classContent += "\t" + algName + typeArguments + " " + algNameLower + "();\n\n";				
		for (Element e: le){
			String methodName = e.getSimpleName().toString();
			String[] args = {methodName, typeArgs, algNameLower};
			classContent += e.asType().accept(new TransformExecutableTypeVisitor(), args);
		}
		classContent += "}";
		return classContent;
	}
	
	String createUtilClass(String folder, Element element, String[] lTypeArgs, String typeArgs) {
		String algName = element.getSimpleName().toString();
		String algNameLower = algName.substring(0, 1).toLowerCase() + algName.substring(1);
		String className = algName + "Trans";
		String classContent = "package " + folder + ";\n\n";
		String typeArguments = "<";
		for (int i = 0; i < lTypeArgs.length; i++) {
			typeArguments += "A" + i;
			if (i < lTypeArgs.length - 1) typeArguments += ", ";
		}
		typeArguments += ">";
		classContent += "import transform." + algName + "Transform;\n";
		classContent += "import " + getPackage(element) + "." + algName + ";\n\n";
		classContent += "public class " + className + typeArguments + " implements " + algName + "Transform" + typeArguments + " {\n\n";
		classContent += "\tprivate " + algName + typeArguments + " alg;\n\n";
		classContent += "\tpublic " + className + "(" + algName + typeArguments + " alg) {this.alg = alg;}\n\n";
		classContent += "\tpublic " + algName + typeArguments + " " + algNameLower + "() {return alg;}\n\n";
		classContent += "}";
		return classContent;
	}
	
	String createSubstTransformClass(String folder, Element element, String[] lTypeArgs, String typeArgs) {
		String algName = element.getSimpleName().toString();
		String algNameLower = algName.substring(0, 1).toLowerCase() + algName.substring(1);
		String className = "G_" + algName + "Transform";
		String argument = "";
		for (int i = 0; i < lTypeArgs.length; i++) {
			if (i > 0) argument += ", ";
			argument += "B" + i;
		}
		String classContent = "package transform;\n\n";
		classContent += "import java.util.function.Function;\nimport java.util.List;\nimport java.util.ArrayList;\n";
		classContent += "import " + getPackage(element) + "." + algName + ";\n\n";
		classContent += "public interface " + className + "<A, " + argument + "> extends " + algName + "<";
		for (int i = 0; i < lTypeArgs.length; i++) {
			if (i > 0) classContent += ", ";
			classContent += "Function<A, B" + i + ">";
		}
		classContent += "> {\n\n\t" + algName +  "<" + argument + "> " + algNameLower + "();\n\n";
		classContent += "\tdefault <B> List<B> substList" + algName + "(List<Function<A, B>> list, A acc) {\n";
		classContent += "\t\tList<B> res = new ArrayList<B>();\n";
		classContent += "\t\tfor (Function<A, B> i : list)\n";
		classContent += "\t\t\tres.add(i.apply(acc));\n";
		classContent += "\t\treturn res;\n\t}\n\n";
		List<? extends Element> le = element.getEnclosedElements();
		for (Element e: le){
			String methodName = e.getSimpleName().toString();
			String[] args = {methodName, typeArgs, algNameLower, algName};
			classContent += e.asType().accept(new SubstTransformTypeVisitor(), args);
		}
		classContent += "}";
		return classContent;
	}
	
	String createQueryClass(String folder, Element element, String[] lTypeArgs, String typeArgs) {
		String algName = element.getSimpleName().toString();
		String classContent = "package " + folder + ";\n\n"
				+ "import library.Monoid;\n"
				+ "import " + getPackage(element) + "." + element.getSimpleName() + ";\n\n" 
				+ "public interface " + algName + "Query<R> extends " + algName + "<";
		for (int i = 0; i < lTypeArgs.length; i++){
			classContent += "R";
			if (i < lTypeArgs.length-1) classContent += ", ";
		}
		classContent += "> {\n\n" + 
				"\tMonoid<R> m();\n\n";
		List<? extends Element> le = element.getEnclosedElements();
		for (Element e: le){
			String methodName = e.getSimpleName().toString();
			String[] args = {methodName, typeArgs};
			classContent += e.asType().accept(new QueryExecutableTypeVisitor(), args);
		}
		classContent += "}";
		return classContent;
	}
	
	String createGeneralQueryClass(String folder, Element element, String[] lTypeArgs, String typeArgs) {
		String algName = element.getSimpleName().toString();
		String typeArguments = "<";
		String[] monoidList = new String[lTypeArgs.length];
		for (int i = 0; i < lTypeArgs.length; i++) {
			typeArguments += "A" + i;
			if (i < lTypeArgs.length - 1) typeArguments += ", ";
			monoidList[i] = "Monoid<A" + i + "> m" + lTypeArgs[i] + "()"; // + i;
		}
		typeArguments += ">";
		String classContent = "package " + folder + ";\n\n"
				+ "import library.Monoid;\n"
				+ "import " + getPackage(element) + "." + element.getSimpleName() + ";\n\n" 
				+ "public interface G_" + algName + "Query" + typeArguments + " extends " + algName + typeArguments + " {\n\n";
		for (int i = 0; i < lTypeArgs.length; i++) {
			classContent += "\t" + monoidList[i] + ";\n";
		}
		classContent += "\n";
		List<? extends Element> le = element.getEnclosedElements();
		for (Element e: le){
			String methodName = e.getSimpleName().toString();
			String[] args = {methodName, typeArgs};
			classContent += e.asType().accept(new GeneralQueryTypeVisitor(), args);
		}
		classContent += "}";
		return classContent;
	}
	
	String createCombinatorClass(String folder, Element element, String typeArgs) {
		String algName = element.getSimpleName().toString();
		String className = "Combine" + algName;
		int typeNum = toList(typeArgs).length;
		String alg1 = "";
		String alg2 = "";
		for (int i = 0; i < typeNum; i++) {
			if (i > 0) {
				alg1 += ", ";
				alg2 += ", ";
			}
			alg1 += "A" + i;
			alg2 += "B" + i;
		}
		String classContent = "package " + folder + ";\n\n"
				+ "import java.util.ArrayList;\n"
				+ "import java.util.List;\n"
				+ "import library.Pair;\n"
				+ "import " + getPackage(element) + "." + element.getSimpleName() + ";\n\n" 
				+ "public class " + className + "<" + alg1 + ", " + alg2 + ">\n\timplements " + algName + "<";
		for (int i = 0; i < typeNum; i++) {
			if (i > 0) classContent += ", ";
			classContent += "Pair<A" + i + ", B" + i + ">";
		}
		classContent += "> {\n\n"
				+ "\tpublic " + algName + "<" + alg1 + "> alg1;\n\tpublic " + algName + "<" + alg2 + "> alg2;\n\n"
				+ "\tpublic " + className + "(" + algName + "<" + alg1 + "> _alg1, " + algName + "<" + alg2 + "> _alg2) {\n"
				+ "\t\talg1 = _alg1;\n\t\talg2 = _alg2;\n\t}\n\n"
				+ "\tprivate <A, B> Pair<List<A>, List<B>> getPairList(List<Pair<A, B>> l) {\n"
				+ "\t\tList<A> l1 = (List<A>)new ArrayList<A>();\n"
				+ "\t\tList<B> l2 = (List<B>)new ArrayList<B>();\n"
				+ "\t\tfor (Pair<A, B> element : l) {\n\t\t\tl1.add(element.a());\n\t\t\tl2.add(element.b());\n\t\t}\n"
				+ "\t\treturn new Pair<List<A>, List<B>>(l1, l2);\n\t}\n\n";
		List<? extends Element> le = element.getEnclosedElements();
		for (Element e: le) {
			String methodName = e.getSimpleName().toString();
			String[] args = {methodName, typeArgs};
			classContent += e.asType().accept(new CombinatorTypeVisitor(), args);
		}
		classContent += "}";
		return classContent;
	}

}
