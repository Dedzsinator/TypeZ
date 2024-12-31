from PIL import Image
import os

def cut_spritemap(spritemap_path, output_dir, sprite_width, sprite_height):
    # Open the sprite map image
    spritemap = Image.open(spritemap_path)
    spritemap_width, spritemap_height = spritemap.size

    # Calculate the number of sprites in the sprite map
    num_sprites_x = spritemap_width // sprite_width
    num_sprites_y = spritemap_height // sprite_height

    # Create the output directory if it doesn't exist
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    # Cut the sprite map into individual sprites
    sprite_count = 0
    for y in range(num_sprites_y):
        for x in range(num_sprites_x):
            # Define the bounding box for the current sprite
            left = x * sprite_width
            upper = y * sprite_height
            right = left + sprite_width
            lower = upper + sprite_height

            # Crop the sprite from the sprite map
            sprite = spritemap.crop((left, upper, right, lower))

            # Save the sprite as an individual image
            sprite_path = os.path.join(output_dir, f'sprite_{sprite_count}.png')
            sprite.save(sprite_path)
            sprite_count += 1

    print(f'Successfully cut {sprite_count} sprites from the sprite map.')

if __name__ == '__main__':
    # Example usage
    spritemap_path = 'gf.png'
    output_dir = 'misc/'
    sprite_width = 140  # Width of each sprite
    sprite_height = 95  # Height of each sprite

    cut_spritemap(spritemap_path, output_dir, sprite_width, sprite_height)