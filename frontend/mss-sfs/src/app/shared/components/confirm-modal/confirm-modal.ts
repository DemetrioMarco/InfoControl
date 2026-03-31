import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-confirm-modal',
  imports: [CommonModule],
  templateUrl: './confirm-modal.html',
  styleUrl: './confirm-modal.css',
})
export class ConfirmModal {

  isOpen   = input<boolean>(false);
  title    = input<string>('Confirmar acción');
  message  = input<string>('¿Estás seguro?');
  subMessage = input<string>('');
  confirmLabel = input<string>('Confirmar');
  confirmClass = input<string>('btn-danger');

  confirmed = output<void>();
  cancelled = output<void>();

}
