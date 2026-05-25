import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteStockService } from '../../../core/services/reporte-stock.service';
import { StockPorSubUbicacion } from '../../../core/models/stock-report.model';

@Component({
  selector: 'app-stock-product-report',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './stock-product-report.html',
  styleUrls: ['./stock-product-report.css']
})
export class StockProductReport implements OnInit {
  private reporteService = inject(ReporteStockService);

  rawLocationData = signal<StockPorSubUbicacion[]>([]);
  searchTerm = signal('');

  // Pivoteamos los datos: De Ubicaciones -> a Productos
  productsSummary = computed(() => {
    const map = new Map<number, any>();
    
    this.rawLocationData().forEach(loc => {
      loc.productos.forEach(prod => {
        if (!map.has(prod.productoId)) {
          map.set(prod.productoId, {
            id: prod.productoId,
            codigo: prod.codigoInterno,
            nombre: prod.nombreProducto,
            stockCalculado: 0,
            distribucion: []
          });
        }
        
        const p = map.get(prod.productoId);
        p.stockCalculado += prod.cantidad;
        p.distribucion.push({
          ubicacion: loc.ubicacionNombre,
          subUbicacion: loc.subUbicacionNombre,
          cantidad: prod.cantidad
        });
      });
    });

    const list = Array.from(map.values());
    const term = this.searchTerm().toLowerCase();
    
    return term 
      ? list.filter(p => p.nombre.toLowerCase().includes(term) || p.codigo.toLowerCase().includes(term))
      : list;
  });

  ngOnInit() {
    this.reporteService.getPorSubUbicacion().subscribe(res => this.rawLocationData.set(res));
  }

  
}
