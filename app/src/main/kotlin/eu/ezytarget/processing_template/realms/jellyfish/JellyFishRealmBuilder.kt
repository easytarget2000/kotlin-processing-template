package eu.ezytarget.processing_template.realms.jellyfish

import eu.ezytarget.processing_template.realms.Realm
import eu.ezytarget.processing_template.realms.RealmBuilder

object JellyFishRealmBuilder : RealmBuilder {

    override fun build(): Realm = JellyFish()
}