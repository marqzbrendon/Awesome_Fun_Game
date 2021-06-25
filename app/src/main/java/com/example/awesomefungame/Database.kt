package com.example.awesomefungame
import android.widget.ArrayAdapter
import com.example.mybudgetapp.LobbyData
import com.example.mybudgetapp.Players
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


open class AppDatabase(private val gameActivity: GameActivity) {
    fun retrieveAllDocuments(
        db: FirebaseFirestore?,
        lobby: String?,
        lobbyData: LobbyData,
        adapter: ArrayAdapter<Any>,
    ) {

        db?.collection("$lobby")?.addSnapshotListener(
                object : EventListener<QuerySnapshot?> {
                    override fun onEvent(
                        snapshots: QuerySnapshot?,
                        e: FirebaseFirestoreException?
                    ) {
                        // Clears any previous data to get the most recent ones
                        lobbyData.players.clear()
                        if (e != null) {
                            println("Listen failed:$e")
                            return
                        }
                        if (snapshots != null) {
                            for (doc in snapshots) {
                                doc.getString("player")?.let {
                                    doc.getDouble("score")?.let {
                                            it1 -> Players(it, it1) }
                                }?.let { lobbyData.players.add(it) }
                            }
                            gameActivity.setUpScoreData(
                                lobbyData,
                                adapter)
                        }
                    }
                })
    }

    fun addPlayerInLobby(db: FirebaseFirestore, player: Players?, lobby: String?, playerKey: String) {
        val newPlayer = HashMap<String, Any>()
        if (player != null) {
            newPlayer["player"] = player.player
            newPlayer["score"] = player.score
        }
        db.collection("$lobby")
            .document("$playerKey")
            .set(newPlayer)
    }

    fun deletePlayerInLobby(db: FirebaseFirestore, playerKey: String?, lobby: String?) {
        if (playerKey != null) {
            db.collection("$lobby")
                .document(playerKey)
                .delete()
        }
    }
}
