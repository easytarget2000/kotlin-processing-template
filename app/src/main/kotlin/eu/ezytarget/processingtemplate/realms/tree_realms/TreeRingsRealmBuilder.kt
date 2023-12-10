package eu.ezytarget.processingtemplate.realms.tree_realms

import eu.ezytarget.processingtemplate.realms.Realm
import eu.ezytarget.processingtemplate.realms.RealmBuilder

object TreeRingsRealmBuilder : RealmBuilder {

    override fun build(): Realm = TreeRingsRealm()

}