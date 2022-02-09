import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class FluidField {
    private double[] density;
    private double[] vx;
    private double[] vy;

    private double[] density0;
    private double[] vx0;
    private double[] vy0;

    private Boolean[] walls;

    private int h;
    private int w;

    private double dt=0.005;
    private double visc = 0.0001;
    private double diff = 0.000001;
    private int N;

    private double t;

//    private boolean mdown = false;
//    private boolean state0 = false;
    public FluidField(int h, int w){
        density=new double[h*w];
        vx=new double[h*w];
        vy=new double[h*w];

        density0=new double[h*w];
        vx0=new double[h*w];
        vy0=new double[h*w];

        walls=new Boolean[h*w];

        this.h=h;
        this.w=w;
        N = h;

        t = 0;
        for(int i=0; i<w; i++){
            for(int j=0; j<h; j++) {
//                if (i!=0 && j!=0 && i!=w-1 && j!=h-1) {
//                    density[IX(i,j)] = Math.random();
//                    vx[IX(i,j)] = Math.random() - 0.5;
//                    vy[IX(i,j)] = Math.random() - 0.5;
//                    walls[IX(i,j)] = false;
//                } else {
                density[IX(i,j)] = 0.0;
                vx[IX(i,j)] = 0;
                vy[IX(i,j)] = 0;
                walls[IX(i,j)] = false;
                //}
            }
        }
        for(int i=0; i<h*w; i++){
            density0[i]=density[i];
            vx0[i]=vx[i];
            vy0[i]=vy[i];
        }

    }

    public void step(){
        vel_step ( N, vy, vx, vy0, vx0, visc, dt );
        dens_step ( N, density, density0, vy, vx, diff, dt );
        for(int i=0; i<h*w; i++) {
            if (i%h< h && i%h>= 0 && i/h<2) {
                t+=0.001;
                if (t>=360)t=0;
                //density[i] = Math.abs(Math.cos(Math.toRadians(t)));
                density[i] = Math.random();
                vx[i] = (5*Math.cos(Math.toRadians(t*2))*Math.random());
                vy[i] = Math.random()-0.5;
            }
            if (i/h>w-4){
                density[i]=0;
            }
        }
    }

    public void diffuse ( int N, int b, double[] x, double[] x0, double diff, double dt )
    {
        int i, j, k;
        double a=dt*diff*N*N;
        for ( k=0 ; k<20 ; k++ ) {
            for ( i=1 ; i<N-1 ; i++ ) {
                for ( j=1 ; j<N-1 ; j++ ) {
                    x[IX(i,j)] = (x0[IX(i,j)] + a*(x[IX(i-1,j)]+x[IX(i+1,j)]+x[IX(i,j-1)]+x[IX(i,j+1)]))/(1+4*a);
                }
            }
            set_bnd ( N-2, b, x );
            set_wall(N, b, x);
        }
    }

    public void advect ( int N, int b, double[] d, double[] d0, double[] u, double[] v, double dt )
    {
        int i, j, i0, j0, i1, j1;
        double x, y, s0, t0, s1, t1, dt0;
        dt0 = dt*N;
        for ( i=1 ; i<N-1 ; i++ ) {
            for ( j=1 ; j<N-1 ; j++ ) {
                x = i-dt0*u[IX(i,j)]; y = j-dt0*v[IX(i,j)];
                if (x<0.5) x=0.5; if (x>N-2+0.5) x=N-2+ 0.5; i0=(int)x; i1=i0+1;
                if (y<0.5) y=0.5; if (y>N-2+0.5) y=N-2+ 0.5; j0=(int)y; j1=j0+1;
                s1 = x-i0; s0 = 1-s1; t1 = y-j0; t0 = 1-t1;
                d[IX(i,j)] = s0*(t0*d0[IX(i0,j0)]+t1*d0[IX(i0,j1)])+s1*(t0*d0[IX(i1,j0)]+t1*d0[IX(i1,j1)]);
            }
        }
        set_bnd ( N-1, b, d );
        set_wall(N, b, d);
    }

    public void dens_step ( int N, double[] x, double[] x0, double[] u, double[] v, double diff, double dt )
    {
        swap( x0, x ); diffuse( N, 0, x, x0, diff, dt );
        swap( x0, x ); advect( N, 0, x, x0, u, v, dt );
    }

    public void vel_step ( int N, double[] u, double[] v, double[] u0, double[] v0,
                           double visc, double dt )
    {
        swap ( u0, u ); diffuse ( N, 1, u, u0, visc, dt );
        swap ( v0, v ); diffuse ( N, 2, v, v0, visc, dt );
        project ( N, u, v, u0, v0 );
        swap ( u0, u ); swap ( v0, v );
        advect ( N, 1, u, u0, u0, v0, dt ); advect ( N, 2, v, v0, u0, v0, dt );
        project ( N, u, v, u0, v0 );
    }

    public void project ( int N, double[] u, double[] v, double[] p, double[] div )
    {
        int i, j, k;
        double h;
        h = 1.0/N;
        for ( i=1 ; i<N-1 ; i++ ) {
            for ( j=1 ; j<N-1 ; j++ ) {
                div[IX(i,j)] = -0.5*h*(u[IX(i+1,j)]-u[IX(i-1,j)]+
                        v[IX(i,j+1)]-v[IX(i,j-1)]);
                p[IX(i,j)] = 0;
            }
        }
        set_bnd ( N-2, 0, div ); set_bnd ( N-2, 0, p );
        for ( k=0 ; k<20 ; k++ ) {
            for ( i=1 ; i<N-1 ; i++ ) {
                for ( j=1 ; j<N-1 ; j++ ) {
                    p[IX(i,j)] = (div[IX(i,j)]+p[IX(i-1,j)]+p[IX(i+1,j)]+
                            p[IX(i,j-1)]+p[IX(i,j+1)])/4;
                }
            }
            set_bnd ( N-1, 0, p );
        }
        for ( i=1 ; i<N-1 ; i++ ) {
            for ( j=1 ; j<N-1 ; j++ ) {
                u[IX(i,j)] -= 0.5*(p[IX(i+1,j)]-p[IX(i-1,j)])/h;
                v[IX(i,j)] -= 0.5*(p[IX(i,j+1)]-p[IX(i,j-1)])/h;
            }
        }
        set_bnd ( N-1, 1, u ); set_bnd ( N-2, 2, v );
        set_wall(N,1, u); set_wall(N,1, v);
    }

    public void set_bnd ( int N, int b, double[] x )
    {
        int i;
        //N=N-2;
        for ( i=1 ; i<=N ; i++ ) {
            if ((b == 1)) {
                x[IX(0, i)] = -x[IX(1, i)];
            } else {
                x[IX(0, i)] = x[IX(1, i)];
            }
            if ((b == 1)) {
                x[IX(N+1,i)] = -x[IX(N,i)];
            } else {
                x[IX(N+1,i)] = x[IX(N,i)];
            }
            if ((b == 2)) {
                x[IX(i,0 )] = -x[IX(i,1)];
            } else {
                x[IX(i, 0)] = x[IX(i,1)];
            }
            if ((b == 2)) {
                x[IX(i,0 )] = -x[IX(i,N)];
            } else {
                x[IX(i, 0)] = x[IX(i,N)];
            }

        }
        x[IX(0 ,0 )] = 0.5*(x[IX(1,0 )]+x[IX(0 ,1)]);
        x[IX(0 ,N+1)] = 0.5*(x[IX(1,N+1)]+x[IX(0 ,N )]);
        x[IX(N+1,0 )] = 0.5*(x[IX(N,0 )]+x[IX(N+1,1)]);
        x[IX(N+1,N+1)] = 0.5*(x[IX(N,N+1)]+x[IX(N+1,N )]);
    }

    public void set_wall ( int N, int b, double[] x)
    {
        int i, j;
        //N=N-4;
        for ( i=1 ; i<N-1 ; i++ ){
            for ( j=1; j<N-1; j++) {
                if ((b == 1) && walls[IX(i,j)]) {
                    x[IX(i, j)] = -x[IX(i+1, j)];
                } else {
                    x[IX(i, j)] = x[IX(i, j)];
                }
                if ((b == 1) && walls[IX(i,j)]) {
                    x[IX(i+1, j)] = -x[IX(i, j)];
                } else {
                    x[IX(i, j)] = x[IX(i, j)];
                }
                if ((b == 2) && walls[IX(i,j)]) {
                    x[IX(i, j)] = -x[IX(i, j+1)];
                } else {
                    x[IX(i, j)] = x[IX(i, j)];
                }
                if ((b == 2) && walls[IX(i,j)]) {
                    x[IX(i, j)] = -x[IX(i, j-1)];
                } else {
                    x[IX(i, j)] = x[IX(i, j)];
                }
            }

        }
    }

    public void swap(double[] x, double[] x0){
        double hold;
        for (int i=0; i<x.length; i++){
            hold=x[i];
            x[i]=x0[i];
            x0[i]=hold;
        }
    }

    public int IX(int i,int j){
        if(j*h+i<w*h) return j*h+i; else return 0 ;
    }

    public int getH() {
        return h;
    }

    public int getW() {
        return w;
    }

    public double[] getDensityArr(){ return density; }
    public double[] getVxArr(){ return vx; }
    public double[] getVyArr(){ return vy; }
    public Boolean[] getWalls(){ return walls; }

    public Boolean getWall(int i, int j){ return walls[IX(i,j)]; }
    public double getDensity(int i, int j){ return density[IX(i,j)]; }
    public double getVx(int i, int j){ return vx[IX(i,j)]; }
    public double getVy(int i, int j){ return vy[IX(i,j)]; }

    public void setWall(int i, int j, boolean b){ walls[IX(i,j)]=b; }
    public void setDensity(int i, int j, double d){ density[IX(i,j)]=d; }
    public void setVx(int i, int j, double v){ vx[IX(i,j)]=v; }
    public void setVy(int i, int j, double v){ vy[IX(i,j)]=v; }


//    @Override
//    public void mouseDragged(MouseEvent e) {
//
//        int x = (int)(e.getX()*((double)w/(double)600));
//        int y = (int)((e.getY()-28)*((double)h/(double)600));
//        if (!mdown) {state0 = walls[IX(y,x)];}
//        mdown = true;
//        walls[IX(y,x)]=!state0;
//        if(y+1<h) walls[IX(y+1,x)]=!state0;
//        if(y-1>=0) walls[IX(y-1,x)]=!state0;
//        if(x+1<w) walls[IX(y,x+1)]=!state0;
//        if(x-1>=0) walls[IX(y,x-1)]=!state0;
//    }
//
//    @Override
//    public void mouseMoved(MouseEvent e) {
//        mdown = false;
//    }
}
