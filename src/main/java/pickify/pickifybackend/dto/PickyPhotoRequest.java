package pickify.pickifybackend.dto;

public record PickyPhotoRequest(
        String photoUrl,
        String langType
) {
}
