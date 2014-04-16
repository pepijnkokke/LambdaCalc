package lambdacalc.impl;

import static lombok.AccessLevel.PRIVATE;
import lambdacalc.DeBruijn;
import lambdacalc.DeBruijnBuilder;
import lambdacalc.DeBruijnClosedDomain;
import lambdacalc.DeBruijnIsClosed;
import lambdacalc.DeBruijnTypeOf;
import lambdacalc.Index;
import lambdacalc.Symbol;
import lambdacalc.Type;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

@RequiredArgsConstructor
@FieldDefaults(makeFinal=true,level=PRIVATE)
public final class IDeBruijnClosedDomain implements DeBruijnClosedDomain {

	Type type;
	DeBruijnTypeOf typeOf;
	DeBruijnBuilder builder;
	DeBruijnIsClosed isClosed;
	
	@Override
	public final Iterable<DeBruijn> domainOf(DeBruijn e) {
		return
			ImmutableList.copyOf(
				Iterables.filter(e.accept(new IDeBruijnSubterms()).build(),
					new Predicate<DeBruijn>() {
						@Override
						public final boolean apply(DeBruijn e) {
							return typeOf.typeOf(e).equals(type) && isClosed.isClosed(e);
						}
					}));
	}

	@RequiredArgsConstructor
	@FieldDefaults(makeFinal=true,level=PRIVATE)
	private final class IDeBruijnSubterms implements
		DeBruijn.Visitor<ImmutableList.Builder<DeBruijn>> {
		
		@Override
		public ImmutableList.Builder<DeBruijn> abstraction(Type type, DeBruijn body) {
			val body1 = body.accept(this);
			body1.add(builder.abstraction(type,body));
			return body1;
		}

		@Override
		public ImmutableList.Builder<DeBruijn> application(DeBruijn fun, DeBruijn arg) {
			val fun1 = fun.accept(this);
			val arg1 = arg.accept(this);
			fun1.addAll(arg1.build());
			fun1.add(builder.application(fun, arg));
			return fun1;
		}

		@Override
		public ImmutableList.Builder<DeBruijn> variable(Index i) {
			return ImmutableList.<DeBruijn> builder();
		}

		@Override
		public ImmutableList.Builder<DeBruijn> constant(Symbol s) {
			val s1 = ImmutableList.<DeBruijn> builder();
			s1.add(builder.constant(s));
			return s1;
		}
	}
}
