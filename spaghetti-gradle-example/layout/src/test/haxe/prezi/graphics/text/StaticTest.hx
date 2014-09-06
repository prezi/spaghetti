package prezi.graphics.text;

import org.hamcrest.MatchersBase;

import prezi.graphics.core.Core;

class StaticTest extends MatchersBase {
	@Test
	public function testStatic() {
		assertThat(Core.giveMeANumber(), is(42));
	}
}
