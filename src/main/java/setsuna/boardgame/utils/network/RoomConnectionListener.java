package setsuna.boardgame.utils.network;

/** Réactions de l'interface à la création/jonction d'une salle, implémentées par le menu **/
public interface RoomConnectionListener{
    void onRoomEntered(int roomId);

    void onRoomJoinFailed();
}
