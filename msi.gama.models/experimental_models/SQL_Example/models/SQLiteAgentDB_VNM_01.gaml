/**
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 *  Description: 
 *  Select data from sqlite and create agent from selected data 
 */

model AgentDB_VNM_01

/* Insert your model definition here */

  
global {
    var numAgent type:int;
	var BOUNDS type:map init: ['dbtype'::'sqlite','database'::'../includes/bph.sqlite'
								,"select"::	"select geometry  from bph where id_2=38253 or id_2=38254;"
								,'srid'::'4326'	// optional 						
							  ];
	var PARAMS type:map init: ['dbtype'::'sqlite','database'::'../includes/bph.sqlite'];
	var LOCATIONS type:string init: "select ID_4, Name_4, geometry from bph where id_2=38253 or id_2=38254;";
	geometry shape <- envelope( BOUNDS);
	init {
		create species: toto number: 1  
		{ 
			create species:locations from: list(self select [params:: PARAMS, select:: LOCATIONS]) with:[ id:: int("id_4"), custom_name:: "name_4", geo::geometry("geometry")]{ 
				set shape value: geo;
			}
		}
		 
	}
   
}   
environment bounds: BOUNDS ;
entities {   
	species toto skills: [SQLSKILL]
	 {  
		//Nothing
	 } 
	
	species locations {
		var id type: int;
		var custom_name type: string;
		var geo type:geometry; 
		var color type: rgb init: rgb([rnd(255),rnd(255),0]);


		reflex test{
				do write message: ' id : ' + (id) + '; custom_name: ' + (custom_name);
		}
	}
}      

experiment default_expr type: gui {
	output {
		monitor numberAgent_ value: numAgent;
		monitor abc value:world.shape;
		display GlobalView {
			species locations transparency: 0 ;
		}
	}
}

