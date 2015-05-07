# Shy
Shy is a framework automatically generating Object Algebras Queries and Transformations based on generic OA Interfaces. The library has been tested in JRE 8 in Eclipse. 

For a complete example of writing object algebras code with Shy, please refer [this](https://github.com/JasonCHU/ObjectAlgebraFramework/tree/master/ObjectAlgebras) repository. 

To use this library, download Library.jar file at the main repository, or you may also export the library yourself from the Java project in the repo. 

Include Library.jar in the Java build path of your project. Also include Library.jar in the factory path of your project, that is, Java Compiler - Annotation Processing - Factory Path if you are using Eclipse. 

Now create your first interface with @Algebra annotation. All default extended OA interfaces such as queries, transformations will be created at their corresponding packages. If they are not created automatically, build your project manually. Another possible reason is that the auto-generated packages are hidden by default, you may set the default generated source directory to be "src". In Eclipse, that is at the setting of Java Compiler - Annotation Processing - Generated Source Directory. Warning: This library may pollute the packages as 4 packages namely "combinator", "query", "transform" and "util" will be created at the root. 

```java
import com.zewei.annotation.processor.Algebra;

@Algebra
public interface ExpAlg<Exp> {
	Exp Var(String s);
	Exp Lit(int i);
	Exp Add(Exp e1, Exp e2);
}
```
