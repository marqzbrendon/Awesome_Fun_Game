package com.example.awesomefungame
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.mybudgetapp.LobbyPlayers
import com.example.mybudgetapp.PeoplePlaying
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


open class AppDatabase(private val gameActivity: GameActivity) {
    fun retrieveAllDocuments(
        db: FirebaseFirestore?,
        lobby: String?,
        lobbyData: LobbyPlayers,
        adapter: ArrayAdapter<Any>,
        lvPlayers: ListView,
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
                                            it1 -> PeoplePlaying(it, it1) }
                                }?.let { lobbyData.players.add(it) }
                            }
                            gameActivity.setUpScoreData(
                                lobbyData,
                                adapter,
                                lvPlayers)
                        }
                    }
                })
    }

    fun addPlayerInLobby(db: FirebaseFirestore, player: Player, lobby: String?, playerKey: String) {
        val newPlayer = HashMap<String, Any>()
        newPlayer["player"] = player.name
        newPlayer["score"] = player.score
        db.collection("$lobby")
            .document(playerKey)
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
