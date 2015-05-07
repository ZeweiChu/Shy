package com.zewei.annotation.processor;

import javax.lang.model.element.Element;
import javax.lang.model.type.*;

public class DeclaredTypeVisitor implements TypeVisitor<String, Element> {

	@Override
	public String visitDeclared(DeclaredType t, Element p) {
		String res = t.getTypeArguments().toString().replace(" ", "");
		int len = res.length();
		res = res.substring(1, len-1);
		return res;
	}

	@Override
	public String visit(TypeMirror t, Element p) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String visit(TypeMirror t) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String visitPrimitive(PrimitiveType t, Element p) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String visitNull(NullType t, Element p) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String visitArray(ArrayType t, Element p) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String visitError(ErrorType t, Element p) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String visitTypeVariable(TypeVariable t, Element p) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String visitWildcard(WildcardType t, Element p) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String visitNoType(NoType t, Element p) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String visitUnknown(TypeMirror t, Element p) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String visitUnion(UnionType t, Element p) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String visitIntersection(IntersectionType t, Element p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitExecutable(ExecutableType t, Element p) {
		// TODO Auto-generated method stub
		return null;
	}
}