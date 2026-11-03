package com.audiobookshelf.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NextUnfinishedEpisodeTest {
  private fun ep(id: String, publishedAt: Long?) =
    PodcastEpisode(id, 0, null, null, id, null, null, null, publishedAt, null, null, null, null, null, null, null)

  private fun next(episodes: List<PodcastEpisode>, after: String?, finished: Set<String> = emptySet()) =
    findNextUnfinishedEpisode(episodes, after) { it.id in finished }?.id

  // Server list is newest first, as the API returns it
  private val episodes = listOf(ep("e3", 3000L), ep("e2", 2000L), ep("e1", 1000L))

  @Test
  fun sortsAscendingByPublishedAt() {
    assertEquals("e2", next(episodes, "e1"))
    assertEquals("e3", next(episodes, "e2"))
  }

  @Test
  fun withoutCurrentEpisodeReturnsOldestUnfinished() {
    assertEquals("e1", next(episodes, null))
    assertEquals("e2", next(episodes, null, finished = setOf("e1")))
  }

  @Test
  fun unknownCurrentEpisodeFallsBackToOldestUnfinished() {
    assertEquals("e1", next(episodes, "gone"))
  }

  @Test
  fun skipsFinishedEpisodes() {
    assertEquals("e3", next(episodes, "e1", finished = setOf("e2")))
  }

  @Test
  fun lastEpisodeHasNoNext() {
    assertNull(next(episodes, "e3"))
  }

  @Test
  fun allFollowingFinishedHasNoNextAndDoesNotWrapAround() {
    assertNull(next(episodes, "e1", finished = setOf("e2", "e3")))
  }

  @Test
  fun missingPublishedAtCountsAsOldest() {
    val withNull = listOf(ep("new", 2000L), ep("undated", null), ep("old", 1000L))
    assertEquals("undated", next(withNull, null))
    assertEquals("old", next(withNull, "undated"))
    assertEquals("new", next(withNull, "old"))
  }

  @Test
  fun equalDatesKeepServerOrder() {
    val same = listOf(ep("a", 1000L), ep("b", 1000L))
    assertEquals("b", next(same, "a"))
  }

  @Test
  fun emptyListHasNoNext() {
    assertNull(next(emptyList(), null))
  }
}
