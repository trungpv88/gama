/**
 *  Equations_Predefined.gaml
 *  Author: Benoit Gaudou
 *  Description: Presentation of all the predefined equation systems.
 *  Comparaison with hand-written systems to test them.
 */
model EDO_predefined

global {
	float mu <- 0.02;
	float alpha <- 35.842;
	float gamma <- 100.0;
	float beta0 <- 1884.95;
	float beta1 <- 0.255;
	float hKR4 <- 0.01;
	init {
		create preSI  with: [h::0.1,N::500,I::1.0];
		create userSI with: [h::0.1,N::500,I::1.0];

		create preSIS  with: [h::0.1,N::500,I::1.0];
		create userSIS with: [h::0.1,N::500,I::1.0];
				
		create preSIR  with: [h::0.1,N::500,I::1.0];
		create userSIR with: [h::0.1,N::500,I::1.0];
		
		create preSIRS  with: [h::0.1,N::500,I::1.0];
		create userSIRS with: [h::0.1,N::500,I::1.0];	
		
		create preSEIR  with: [h::0.1,N::500,I::1.0];
		create userSEIR with: [h::0.1,N::500,I::1.0];		
		
		create preLV  with: [h::0.1,x::2.0,y::2.0];
		create userLV with: [h::0.1,x::2.0,y::2.0];			
	}
}

entities {
	species preSI {
		float t;
    	int N;
		float I ; 
		float S <- N - I; 
   		float h;
   		float beta<-0.4;

		// must be followed with exact order S, I, t  and N,beta
		equation eqSI type: SI vars: [S,I,t] params: [N,beta] {}

		solve eqSI method:rk4 step:h;
	}

	species userSI {
		float t;
    	int N;
		float I ; 
		float S <- N - I; 
   		float h;
   		float beta<-0.4;
		
		equation eqSI {
			diff(S,t) = -beta * S * I / N ;
			diff(I,t) = beta * S * I / N ;
		}		

		solve eqSI method:rk4 step:h;
	}


	species preSIS {
		float t;
    	int N;
		float I ; 
		float S <- N - I; 
   		float h;
   		float beta<-0.4;
   		float gamma<-0.01;    		

		// must be followed with exact order S, I, t  and N,beta
		equation eqSIS type: SIS vars: [S,I,t] params: [N,beta,gamma] {}

		solve eqSIS method:rk4 step:h;
	}

	species userSIS {
		float t;
    	int N;
		float I ; 
		float S <- N - I; 
   		float h;
   		float beta<-0.4;
   		float gamma<-0.01;    		
		
		equation eqSIS {
			diff(S,t) = -beta * S * I / N + gamma * I;
			diff(I,t) = beta * S * I / N - gamma * I;
		}		

		solve eqSIS method:rk4 step:h;
	}
	
	
	species preSIR {
		float t;
    	int N;
		float I ; 
		float S <- N - I; 
		float R <- 0.0; 
   		float h;
   		float beta<-0.4;
   		float gamma<-0.01; 

		// must be followed with exact order S, I, R, t  and N,beta,delta
		equation eqSIR type:SIR vars:[S,I,R,t] params:[N,beta,gamma]{}

		solve eqSIR method:rk4 step:h;
	}

	species userSIR{
		float t;
    	int N;
		float I ; 
		float S <- N - I; 
		float R <- 0.0; 
   		float h;
   		float beta<-0.4;
   		float gamma<-0.01; 				
		
		equation eqSIR {
			diff(S,t) = (- beta * S * I / N);
			diff(I,t) = (beta * S * I / N) - (gamma * I);
			diff(R,t) = (gamma * I);
		}		

		solve eqSIR method:rk4 step:h;
	}


	species preSIRS {
		float t;
    	int N;
		float I ; 
		float S <- N - I; 
		float R <- 0.0; 
   		float h;
   		float beta<-0.4;
   		float gamma<-0.01; 
   		float omega <- 0.05;
   		float mu <- 0.01;

		// must be followed with exact order S, I, R, t  and N,beta,delta
		equation eqSIRS type: SIRS vars: [S,I,R,t] params: [N,beta,gamma,omega,mu]{}

		solve eqSIRS method:rk4 step:h;
	}

	species userSIRS {
		float t;
    	int N;
		float I ; 
		float S <- N - I; 
		float R <- 0.0; 
   		float h;
   		float beta<-0.4;
   		float gamma<-0.01; 	
   		float omega <- 0.05;
   		float mu <- 0.01;   					
		
		equation eqSIRS {
			 diff(S,t) = mu * N + omega * R + - beta * S * I / N - mu * S ;
			 diff(I,t) = beta * S * I / N - gamma * I - mu * I ;
			 diff(R,t) = gamma * I - omega * R - mu * R ;
		}		

		solve eqSIRS method:rk4 step:h;
	}


	species preSEIR {
		float t;
    	int N;
		float S <- N - I;     	
		float E <- 0.0;
		float I ; 
		float R <- 0.0; 
   		float h;
   		float beta<-0.4;
   		float gamma<-0.01; 
   		float sigma <- 0.05;
   		float mu <- 0.01;

		// must be followed with exact order S, E, I, R, t  and N,beta,gamma,sigma,mu
		equation eqSEIR type: SEIR vars: [S,E,I,R,t] params: [N,beta,gamma,sigma,mu] {}		

		solve eqSEIR method:rk4 step:h;
	}

	species userSEIR {
		float t;
    	int N;
		float S <- N - I;     	
		float E <- 0.0;
		float I ; 
		float R <- 0.0; 
   		float h;
   		float beta<-0.4;
   		float gamma<-0.01; 	
   		float sigma <- 0.05;
   		float mu <- 0.01;   					
		
		equation eqSEIR {
			diff(S,t) = mu * N - beta * S * I / N - mu * S ;
			diff(E,t) = beta * S * I / N - mu * E - sigma * E ;
			diff(I,t) = sigma * E - mu * I - gamma * I;
			diff(R,t) = gamma * I - mu * R ;
		}		

		solve eqSEIR method:rk4 step:h;
	}


	species preLV {
		float t;
		float x ; 
		float y ; 
   		float h;
		float alpha <- 0.8 ;
		float beta  <- 0.3 ;
		float gamma <- 0.2 ;
		float delta <- 0.85;

		// must be followed with exact order x, y, t  and  alpha,beta,delta,gamma
		equation eqLV type: LV vars: [x,y,t] params: [alpha,beta,delta,gamma] {}

		solve eqLV method:rk4 step:h;
	}

	species userLV {
		float t;
		float x ; 
		float y ; 
   		float h;
		float alpha <- 0.8 ;
		float beta  <- 0.3 ;
		float gamma <- 0.2 ;
		float delta <- 0.85;
		
		equation eqLV { 
			diff(x,t) =   x * (alpha - beta * y);
			diff(y,t) = - y * (delta - gamma * x);
        }		

		solve eqLV method:rk4 step:h;
	}

}

experiment examples type : gui {
	output {		
		display SI refresh_every : 1 {
			chart 'examplePreSI' type : series background : rgb('lightGray') position: {0,0} size:{1,0.5} {
				data "S" value : first(preSI).S color : rgb('green');
				data "I" value : first(preSI).I color : rgb('red');
			}
			chart 'examplesUserSI' type : series background : rgb('lightGray') position: {0,0.5} size:{1,0.5} {
				data "S" value : first(userSI).S color : rgb('green');
				data "I" value : first(userSI).I color : rgb('red');
			}
		}

		display SISs refresh_every : 1 {
			chart 'examplePreSIS' type : series background : rgb('lightGray') position: {0,0} size:{1,0.5} {
				data "S" value : first(preSIS).S color : rgb('green');
				data "I" value : first(preSIS).I color : rgb('red');
			}
			chart 'examplesUserSIS' type : series background : rgb('lightGray') position: {0,0.5} size:{1,0.5} {
				data "S" value : first(userSIS).S color : rgb('green');
				data "I" value : first(userSIS).I color : rgb('red');
			}			
		}
		
		display SIR refresh_every : 1 {
			chart 'examplePreSIR' type : series background : rgb('lightGray') position: {0,0} size:{1,0.5} {
				data "S" value : first(preSIR).S color : rgb('green');
				data "I" value : first(preSIR).I color : rgb('red');
				data "R" value : first(preSIR).R color : rgb('blue');
			}
			chart 'examplesUserSIR' type : series background : rgb('lightGray') position: {0,0.5} size:{1,0.5} {
				data "S" value : first(userSIR).S color : rgb('green');
				data "I" value : first(userSIR).I color : rgb('red');
				data "R" value : first(userSIR).R color : rgb('blue');
			}			
		}

		display SIRS refresh_every : 1 {
			chart 'examplePreSIRS' type : series background : rgb('lightGray') position: {0,0} size:{1,0.5} {
				data "S" value : first(preSIRS).S color : rgb('green');
				data "I" value : first(preSIRS).I color : rgb('red');
				data "R" value : first(preSIRS).R color : rgb('blue');
			}
			chart 'examplesUserSIRS' type : series background : rgb('lightGray') position: {0,0.5} size:{1,0.5} {
				data "S" value : first(userSIRS).S color : rgb('green');
				data "I" value : first(userSIRS).I color : rgb('red');
				data "R" value : first(userSIRS).R color : rgb('blue');
			}			
		}

		display SEIR refresh_every : 1 {
			chart 'examplePreSEIR' type : series background : rgb('lightGray') position: {0,0} size:{1,0.5} {
				data "S" value : first(preSEIR).S color : rgb('green');
				data "E" value : first(preSEIR).E color : rgb('yellow');
				data "I" value : first(preSEIR).I color : rgb('red');
				data "R" value : first(preSEIR).R color : rgb('blue');
			}
			chart 'examplesUserSEIR' type : series background : rgb('lightGray') position: {0,0.5} size:{1,0.5} {
				data "S" value : first(userSEIR).S color : rgb('green');
				data "E" value : first(userSEIR).E color : rgb('yellow');				
				data "I" value : first(userSEIR).I color : rgb('red');
				data "R" value : first(userSEIR).R color : rgb('blue');
			}
		}

		display LV refresh_every : 1 {
			chart 'examplePreLV' type : series background : rgb('lightGray') position: {0,0} size:{1,0.5} {
				data "x" value : first(preLV).x color : rgb('yellow');
				data "y" value : first(preLV).y color : rgb('blue');
			}
			chart 'examplesUserLV' type : series background : rgb('lightGray') position: {0,0.5} size:{1,0.5} {
				data "x" value : first(userLV).x color : rgb('yellow');
				data "y" value : first(userLV).y color : rgb('blue');
			}			
		}						
	}
}

experiment diff_predefined_defined_by_user type : gui {
	output {
		display diff refresh_every : 1 {
			chart 'diffSI' type : series background : rgb('lightGray')  position: {0,0} size:{0.5, 0.33} {
				data "dS" value : (first(userSI).S - first(preSI).S) color : rgb('yellow');
				data "dI" value : (first(userSI).I - first(preSI).I) color : rgb('blue');
			}
			chart 'diffSIS' type : series background : rgb('lightGray') position: {0.5,0} size:{0.5, 0.33} {
				data "dS" value : (first(userSIS).S - first(preSIS).S) color : rgb('yellow');
				data "dI" value : (first(userSIS).I - first(preSIS).I) color : rgb('blue');
			}
			chart 'diffSIR' type : series background : rgb('lightGray') position: {0,0.33} size:{0.5, 0.33} {
				data "dS" value : (first(userSIR).S - first(preSIR).S) color : rgb('yellow');
				data "dI" value : (first(userSIR).I - first(preSIR).I) color : rgb('blue');
				data "dR" value : (first(userSIR).R - first(preSIR).R) color : rgb('red');
			}		
			chart 'diffSIRS' type : series background : rgb('lightGray') position: {0.5,0.33} size:{0.5, 0.33} {
				data "dS" value : (first(userSIRS).S - first(preSIRS).S) color : rgb('yellow');
				data "dI" value : (first(userSIRS).I - first(preSIRS).I) color : rgb('blue');
				data "dR" value : (first(userSIRS).R - first(preSIRS).R) color : rgb('red');
			}	
			chart 'diffSEIR' type : series background : rgb('lightGray') position: {0,0.66} size:{0.5, 0.33} {
				data "dS" value : (first(userSEIR).S - first(preSEIR).S) color : rgb('yellow');
				data "dE" value : (first(userSEIR).E - first(preSEIR).E) color : rgb('yellow');				
				data "dI" value : (first(userSEIR).I - first(preSEIR).I) color : rgb('blue');
				data "dR" value : (first(userSEIR).R - first(preSEIR).R) color : rgb('red');
			}	
			chart 'diffLV' type : series background : rgb('lightGray') position: {0.5,0.66} size:{0.5, 0.33} {
				data "dx" value : (first(userLV).x - first(preLV).x) color : rgb('yellow');
				data "dy" value : (first(userLV).y - first(preLV).y) color : rgb('red');				
			}					
		}	
	}
}
