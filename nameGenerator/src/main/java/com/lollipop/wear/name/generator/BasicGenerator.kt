package com.lollipop.wear.name.generator

import com.lollipop.wear.name.NameGenerator

abstract class BasicGenerator : NameGenerator.Generator {

    protected abstract val adjectiveList: List<String>

    protected abstract val nounList: List<String>


    override fun randomAdjective(): String {
        return adjectiveList.random()
    }

    override fun randomNoun(): String {
        return nounList.random()
    }
}