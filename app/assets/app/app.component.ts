import { Component } from '@angular/core';
@Component({
  selector: 'my-app',
  template: `
    <div class="container">
        <navbar></navbar>   
        <router-outlet></router-outlet>
    </div> 
  `
})
export class AppComponent { }