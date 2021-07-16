package com.puroayurveda.puroayurveda.services

import com.puroayurveda.generated.types.Show

interface ShowsService {
    fun shows(): List<Show>
}