package prezi.graphics.text;

import org.hamcrest.MatchersBase;

class ValuesTest extends MatchersBase {
	@Test
	public function test() {
		assertThat(Values.HELLO, is(12));
	}
}
