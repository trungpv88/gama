/**
 *  Author: Arnaud Grignard
 *  Digital Model Elevation representation of Hanoi city.
 */
model Hanoi_DEM

global{
	file dem parameter: 'DEM' <- file('../includes/DEM_Hanoi/DEM.png');
    file map_texture parameter: 'Texture' <- file('../includes/DEM_Hanoi/maps.jpg');
    file street_texture parameter: 'Texture' <- file('../includes/DEM_Hanoi/street.jpg');
    float z_factor parameter: 'z_factor' min:0.1 <- 0.1;
    
	//The size of the environment must fit with the size of the DEM file.
    geometry shape <- rectangle(500,395);
}

experiment Display type: gui {
	output {
		
		//Display a DEM model with its associated texture coming from google maps for instance.
		display HanoiMap  type: opengl ambient_light:255 draw_env: true{
			graphics 'DEMTextured' {
				draw dem(dem, map_texture,z_factor);
			}
		}
		
		//Display a DEM model with its associated texture coming from google maps for instance.
		display HanoiStreet  type: opengl ambient_light:255 draw_env: false {
			graphics 'DEMTextured' {
				draw dem(dem, street_texture,z_factor);
			}
		}
		//Display a DEM model with the original color of the DEM file.
		display HanoiDEM  type: opengl ambient_light:255 draw_env: false{
			graphics 'DEM' {
				draw dem(dem,z_factor);
			}
		}
		
	}
}
