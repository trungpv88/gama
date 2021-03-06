model overlapping_grid

global {
	string shp_bounds <- '../gis/one_road_bounds.shp';
	string shp_road <- '../gis/one_road_overlapping.shp';
	
	init {
		create road from: shp_road;
		
		write 'world shape with width: ' + (string(shape.width)) + '; height: ' + (string(shape.height));
		
		/*
		 *  a grid of 5m * 5m
		 * 723 / 5 � 144
		 * 545 / 5 � 109
		 */
		let grid_dimension type: point value: {144, 109};
		let env_grid type: matrix value: shape as_grid grid_dimension;
		
		let i type: int value: 0;
		let j type: int value: 0;
		loop times: grid_dimension.x {
			set j value: 0;
			
			loop times: grid_dimension.y {
				create density_grid with: [ shape :: ( env_grid at ({i, j}) ) ];
				
				set j value: j + 1;
			}
			
			set i value: i + 1;
		}
	}
}

environment bounds: shp_bounds;

entities {
	
	species density_grid {
		
		rgb color;
		
		init {
			if !(empty(road overlapping self)) {
				set color value: rgb('blue');
			} else { set color value: rgb('white'); }
		}
		
		aspect base {
			draw shape: geometry color: color;
		}
	}
	
	species road {
		
		aspect base {
			draw shape: geometry color: rgb('black');
		}
	}
}

experiment test type: gui {
	output {
		display default {
			species road aspect: base;
			species density_grid aspect: base transparency: 0.5;
		}
	}
}