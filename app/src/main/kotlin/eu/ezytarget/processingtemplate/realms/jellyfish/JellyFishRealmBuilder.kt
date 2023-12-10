package eu.ezytarget.processingtemplate.realms.jellyfish

import eu.ezytarget.processingtemplate.realms.Realm
import eu.ezytarget.processingtemplate.realms.RealmBuilder

object JellyFishRealmBuilder : RealmBuilder {

    override fun build(): Realm = JellyFish()
}