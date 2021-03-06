package lambdacalc.impl;

import static lombok.AccessLevel.PRIVATE;
import lambdacalc.DeBruijn;
import lambdacalc.DeBruijnToType;
import lambdacalc.Index;
import lambdacalc.Symbol;
import lambdacalc.Type;
import lambdacalc.TypeBuilder;
import lambdacalc.Types;
import lambdacalc.DeBruijn.Visitor;
import lambdacalc.TypeError;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(makeFinal=true,level=PRIVATE)
public final class IDeBruijnToType implements Visitor<Type>, DeBruijnToType {

	TypeBuilder builder;
	
	@Override
	public final Type typeOf(DeBruijn expr) {
		return expr.accept(this);
	}

	@Override
	public final Type abstraction(Type type, DeBruijn body) {
		return builder.function(type,body.accept(this));
	}

	@Override
	public final Type application(DeBruijn fun, DeBruijn arg) {
		return fun.accept(this).accept(new TypeBuilder() {
			@Override
			public Type constant(String name) {
				if (name.equals("*")) {
					return Types.STAR;
				}
				else {
					throw new TypeError("cannot apply function of type '%s'", name);
				}
			}
			@Override
			public final Type function(Type a, Type b) {
				return b;
			}
		});
	}

	@Override
	public final Type variable(Index i) {
		return i.getType();
	}

	@Override
	public final Type constant(Symbol s) {
		return s.getType();
	}

}
