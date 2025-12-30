package com.example.wedding_planner.ui.util

import com.example.wedding_planner.data.model.Task

fun filteredGuestsBySearch(tasks: List<Task>) = tasks.groupBy { it.category }