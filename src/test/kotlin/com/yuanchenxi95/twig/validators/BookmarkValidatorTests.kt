package com.yuanchenxi95.twig.validators

import com.yuanchenxi95.twig.framework.validation.ValidationError
import com.yuanchenxi95.twig.protobuf.api.UpdateBookmarkRequest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class BookmarkValidatorTests {

    companion object {
        private const val BOOKMARK_ID = "1"
    }

    @Test
    fun `updateBookmark no updateMask should throw error`() {
        Assertions.assertThatThrownBy {
            validateUpdateBookmarkRequest(UpdateBookmarkRequest.getDefaultInstance(), BOOKMARK_ID)
        }.isInstanceOf(ValidationError::class.java)
            .hasMessage("Update Mask cannot be null.")
    }
}
