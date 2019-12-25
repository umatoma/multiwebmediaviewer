package io.github.umatoma.multiwebmediaviewer

import com.google.common.truth.Truth.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


object ExampleSpec: Spek({

    describe("Example") {

        it("should return 3") {
            assertThat((1 + 2).toInt()).isEqualTo(3)
        }
    }
})
