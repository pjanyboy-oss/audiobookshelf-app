package com.audiobookshelf.app.media

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ListeningSecondsToSyncTest {
  private val now = 1_000_000L

  @Test
  fun regularSyncSendsElapsedSeconds() {
    assertEquals(5L, listeningSecondsToSync(now - 5_400L, now, force = false))
  }

  @Test
  fun regularSyncSkipsWithoutStartTime() {
    assertNull(listeningSecondsToSync(0L, now, force = false))
  }

  @Test
  fun regularSyncSkipsWithinOneSecond() {
    assertNull(listeningSecondsToSync(now - 999L, now, force = false))
  }

  // finished(): pause() ran right before STATE_ENDED and set lastSyncTime to 0
  @Test
  fun finishedAfterPauseStillSyncs() {
    assertEquals(0L, listeningSecondsToSync(0L, now, force = true))
  }

  // finished(): periodic sync happened less than a second before the end
  @Test
  fun finishedRightAfterSyncStillSyncs() {
    assertEquals(0L, listeningSecondsToSync(now - 200L, now, force = true))
  }

  @Test
  fun finishedCountsElapsedSeconds() {
    assertEquals(3L, listeningSecondsToSync(now - 3_000L, now, force = true))
  }
}
