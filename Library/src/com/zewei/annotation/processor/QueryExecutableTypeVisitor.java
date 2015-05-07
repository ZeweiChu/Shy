package com.zewei.annotation.processor;

import java.util.List;
import javax.lang.model.type.*;

public class QueryExecutableTypeVisitor implements TypeVisitor<String, String[]> {
	
	int arrayContains(String[] ls, String s) {
		int i = 0;
		for (String ts: ls) {
			if (s.equals(ts)) return i;
			i++;
		}
		return -1;
	}
	
	@Override
	public String visitExecutable(ExecutableType t, String[] p) {
		String methodName = p[0];
		String[] lTypeArgs = p[1].split(",");
		String[] lListTypeArgs = new String[lTypeArgs.length];
		for (int i = 0; i < lTypeArgs.length; ++i){
			lListTypeArgs[i] = "java.util.List<" + lTypeArgs[i] + ">";
		}
		
		List<? extends TypeMirror> lp = t.getParameterTypes();
		String returnType = t.getReturnType().toString();
		
		String res = "\tdefault ";
		
		// Make sense.?
		if (arrayContains(lListTypeArgs, returnType) != -1){
			res += "java.util.List<R> ";
		} else if (arrayContains(lTypeArgs, returnType) != -1){
			res += "R ";
		} else res += returnType + " ";
		
		res += methodName + "(";
		
		
		for (int i = 0; i < lp.size(); ++i){
			// contains a list of type variables
			if (arrayContains(lListTypeArgs, lp.get(i).toString()) != -1){
				res += "java.util.List<R> p" + i;
			} else if (arrayContains(lTypeArgs, lp.get(i).toString()) != -1){
				res += "R p" + i;
			} else {
				res += lp.get(i).toString() + " p" + i;
			}
			if (i < lp.size()-1) res += ", ";
		}
		
		res += ") {\n";
		res += "\t\tR res = m().empty();\n";
		for (int i = 0; i < lp.size(); ++i){
			if (arrayContains(lListTypeArgs, lp.get(i).toString()) != -1){
				res += "\t\tres = m().join(res, m().fold(p" + i + "));\n";
			}
			else if (arrayContains(lTypeArgs, lp.get(i).toString()) != -1){
				res += "\t\tres = m().join(res, p" + i + ");\n";
			}
		}
		res += "\t\treturn res;\n";
		res += "\t}\n\n";
		return res;
	}

	@Override
	public String visit(TypeMirror t, String[] p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(TypeMirror t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitPrimitive(PrimitiveType t, String[] p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitNull(NullType t, String[] p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitArray(ArrayType t, String[] p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitDeclared(DeclaredType t, String[] p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitError(ErrorType t, String[] p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitTypeVariable(TypeVariable t, String[] p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitWildcard(WildcardType t, String[] p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitNoType(NoType t, String[] p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitUnknown(TypeMirror t, String[] p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitUnion(UnionType t, String[] p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitIntersection(IntersectionType t, String[] p) {
		// TODO Auto-generated method stub
		return null;
	}
}